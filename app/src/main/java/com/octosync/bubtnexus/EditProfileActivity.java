package com.octosync.bubtnexus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.octosync.bubtnexus.models.ProfileUpdateRequest;
import com.octosync.bubtnexus.models.ProfileUpdateResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPhone, etStudentId, etDepartment, etSemester, etIntake, etAddress;
    private ImageView ivProfile;
    private MaterialButton btnSave, btnCancel;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupClickListeners();
        loadCurrentUserData();
    }

    private void initializeViews() {
        // EditTexts
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStudentId = findViewById(R.id.etStudentId);
        etDepartment = findViewById(R.id.etDepartment);
        etSemester = findViewById(R.id.etSemester);
        etIntake = findViewById(R.id.etIntake);
        etAddress = findViewById(R.id.etAddress);

        // Image and Buttons
        ivProfile = findViewById(R.id.ivProfile);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCancel.setOnClickListener(v -> finish());

        ivProfile.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentUserData() {
        // Load current user data into form
        etName.setText(sessionManager.getUserName());
        etEmail.setText(sessionManager.getUserEmail());
        etPhone.setText(sessionManager.getPhone() != null ? sessionManager.getPhone() : "");
        etStudentId.setText(sessionManager.getStudentId() != null ? sessionManager.getStudentId() : "");
        etDepartment.setText(sessionManager.getDepartment() != null ? sessionManager.getDepartment() : "");
        etSemester.setText(sessionManager.getSemester() != null ? sessionManager.getSemester() : "");
        etAddress.setText(sessionManager.getAddress() != null ? sessionManager.getAddress() : "");
        etIntake.setText(sessionManager.getIntake() != null ? sessionManager.getIntake() : "");

        // Load profile picture from base64 string if exists
        String savedImageBase64 = sessionManager.getProfilePictureUri();
        if (savedImageBase64 != null && !savedImageBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(savedImageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                ivProfile.setImageBitmap(bitmap);
                selectedBitmap = bitmap;
            } catch (Exception e) {
                Log.e(TAG, "Error loading saved profile picture: " + e.getMessage());
                // Use default image if there's an error
                ivProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "Error opening image picker: " + e.getMessage());
            showToast("Cannot open image picker");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Use InputStream to decode the bitmap safely
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    if (inputStream != null) {
                        selectedBitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();

                        if (selectedBitmap != null) {
                            // Resize bitmap to avoid memory issues
                            selectedBitmap = getResizedBitmap(selectedBitmap, 400);
                            ivProfile.setImageBitmap(selectedBitmap);

                            // Convert bitmap to base64 and save to SharedPreferences
                            String base64Image = bitmapToBase64(selectedBitmap);
                            sessionManager.saveProfilePictureUri(base64Image);
                            showToast("Profile picture updated");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading selected image: " + e.getMessage());
                    showToast("Error loading image");
                }
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        String intake = etIntake.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        // Basic validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        showLoading(true);

        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setName(name);
        updateRequest.setEmail(email);
        updateRequest.setPhone(phone.isEmpty() ? null : phone);
        updateRequest.setStudentId(studentId.isEmpty() ? null : studentId);
        updateRequest.setDepartment(department.isEmpty() ? null : department);
        updateRequest.setSemester(semester.isEmpty() ? null : semester);
        updateRequest.setIntake(intake.isEmpty() ? null : intake);
        updateRequest.setAddress(address.isEmpty() ? null : address);

        String token = sessionManager.getToken();
        if (token == null) {
            showToast("Please login again");
            showLoading(false);
            return;
        }

        Log.d(TAG, "Updating profile with data:");
        Log.d(TAG, "Name: " + name);
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Phone: " + phone);
        Log.d(TAG, "Student ID: " + studentId);
        Log.d(TAG, "Department: " + department);
        Log.d(TAG, "Intake: " + intake);
        Log.d(TAG, "Semester: " + semester);

        Call<ProfileUpdateResponse> call = apiService.updateProfile("Bearer " + token, updateRequest);

        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                showLoading(false);

                Log.d(TAG, "Response Code: " + response.code());
                Log.d(TAG, "Response Message: " + response.message());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        ProfileUpdateResponse updateResponse = response.body();
                        Log.d(TAG, "Response Success: " + updateResponse.isSuccess());
                        Log.d(TAG, "Response Message: " + updateResponse.getMessage());

                        if (updateResponse.isSuccess()) {
                            // Update session with new data
                            updateSessionData();
                            showToast("Profile updated successfully");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            String errorMsg = updateResponse.getMessage() != null ?
                                    updateResponse.getMessage() : "Update failed";
                            showToast(errorMsg);
                            Log.e(TAG, "API returned success: false - " + errorMsg);
                        }
                    } else {
                        showToast("Empty response from server");
                        Log.e(TAG, "Response body is null");
                    }
                } else {
                    // Handle HTTP errors
                    String errorMessage = "Failed to update profile";
                    if (response.code() == 401) {
                        errorMessage = "Session expired. Please login again.";
                        sessionManager.clear();
                        redirectToLogin();
                    } else if (response.code() == 422) {
                        errorMessage = "Validation error. Please check your input.";
                    } else if (response.code() == 500) {
                        errorMessage = "Server error. Please try again later.";
                    }

                    // Try to read error body for more details
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                            showToast("Error: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                            showToast(errorMessage);
                        }
                    } else {
                        showToast(errorMessage);
                    }
                    Log.e(TAG, "HTTP Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Profile update failed: " + t.getMessage(), t);
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void updateSessionData() {
        // Update session with form data
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
        String intake = etIntake.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        sessionManager.updateUserName(name);
        sessionManager.updateUserEmail(email);
        sessionManager.updateStudentId(studentId);
        sessionManager.updateUserPhone(phone);
        sessionManager.updateUserDepartment(department);
        sessionManager.updateUserSemester(semester);
        sessionManager.updateIntake(intake);
        sessionManager.updateUserAddress(address);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up bitmap to avoid memory leaks
        if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
            selectedBitmap.recycle();
            selectedBitmap = null;
        }
    }
}