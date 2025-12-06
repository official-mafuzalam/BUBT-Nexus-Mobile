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
import com.octosync.bubtnexus.models.User;
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

    private EditText etName, etEmail, etPhone, etStudentId, etFacultyCode, etDepartment, etSemester, etIntake, etAddress, etDesignation, etCgpa;
    private ImageView ivProfile;
    private MaterialButton btnSave, btnCancel;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;
    private Bitmap selectedBitmap;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupClickListeners();
        loadCurrentUserData();
        setupFieldsByUserType();
    }

    private void initializeViews() {
        // EditTexts
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStudentId = findViewById(R.id.etStudentId);
        etFacultyCode = findViewById(R.id.etFacultyCode);
        etDepartment = findViewById(R.id.etDepartment);
        etSemester = findViewById(R.id.etSemester);
        etIntake = findViewById(R.id.etIntake);
        etAddress = findViewById(R.id.etAddress);
        etDesignation = findViewById(R.id.etDesignation);
        etCgpa = findViewById(R.id.etCgpa);

        // Image and Buttons
        ivProfile = findViewById(R.id.ivProfile);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupFieldsByUserType() {
        boolean isStudent = sessionManager.isStudent();
        boolean isFaculty = sessionManager.isFaculty();

        // Find label views
        View tvStudentIdLabel = findViewById(R.id.tvStudentIdLabel);
        View tvFacultyCodeLabel = findViewById(R.id.tvFacultyCodeLabel);
        View tvSemesterLabel = findViewById(R.id.tvSemesterLabel);
        View tvCgpaLabel = findViewById(R.id.tvCgpaLabel);
        View tvDesignationLabel = findViewById(R.id.tvDesignationLabel);

        // Show/hide student fields
        if (etStudentId != null) {
            etStudentId.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (tvStudentIdLabel != null) {
            tvStudentIdLabel.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }

        // Show/hide faculty fields
        if (etFacultyCode != null) {
            etFacultyCode.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
        if (tvFacultyCodeLabel != null) {
            tvFacultyCodeLabel.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }

        // Show/hide semester field (for students)
        if (etSemester != null) {
            etSemester.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (tvSemesterLabel != null) {
            tvSemesterLabel.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }

        // Show/hide CGPA field (for students)
        if (etCgpa != null) {
            etCgpa.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (tvCgpaLabel != null) {
            tvCgpaLabel.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }

        // Show/hide designation field (for faculty)
        if (etDesignation != null) {
            etDesignation.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
        if (tvDesignationLabel != null) {
            tvDesignationLabel.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        ivProfile.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentUserData() {
        try {
            // Load current user data into form
            etName.setText(sessionManager.getUserName());
            etEmail.setText(sessionManager.getUserEmail());
            etPhone.setText(sessionManager.getPhone() != null ? sessionManager.getPhone() : "");
            etDepartment.setText(sessionManager.getDepartment() != null ? sessionManager.getDepartment() : "");

            // Load user type specific data
            if (sessionManager.isStudent()) {
                etStudentId.setText(sessionManager.getStudentId() != null ? sessionManager.getStudentId() : "");
                etSemester.setText(sessionManager.getSemester() != null ? sessionManager.getSemester() : "");
                etCgpa.setText(sessionManager.getCgpa() != null ? sessionManager.getCgpa() : "");
                etIntake.setText(sessionManager.getIntake() > 0 ? String.valueOf(sessionManager.getIntake()) : "");
            }

            if (sessionManager.isFaculty()) {
                etFacultyCode.setText(sessionManager.getFacultyCode() != null ? sessionManager.getFacultyCode() : "");
                etDesignation.setText(sessionManager.getDesignation() != null ? sessionManager.getDesignation() : "");
            }

            // Load profile picture from session
            loadProfilePicture();
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePicture() {
        try {
            String profilePicturePath = sessionManager.getProfilePicture();
            if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                // Handle Base64 encoded image
                if (profilePicturePath.startsWith("data:image") || profilePicturePath.startsWith("/9j/")) {
                    String base64Data = profilePicturePath;
                    if (profilePicturePath.contains(",")) {
                        base64Data = profilePicturePath.substring(profilePicturePath.indexOf(",") + 1);
                    }

                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        ivProfile.setImageBitmap(bitmap);
                        selectedBitmap = bitmap;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile picture: " + e.getMessage());
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "Error opening image picker: " + e.getMessage());
            Toast.makeText(this, "Cannot open image picker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    if (inputStream != null) {
                        selectedBitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();

                        if (selectedBitmap != null) {
                            // Resize bitmap to avoid memory issues
                            selectedBitmap = getResizedBitmap(selectedBitmap, 400);
                            ivProfile.setImageBitmap(selectedBitmap);
                            Toast.makeText(this, "Profile picture selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading selected image: " + e.getMessage());
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        if (image == null) return null;

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

    private void updateProfile() {
        // Get form values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
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

        // Get user type specific fields
        String studentId = "";
        String semester = "";
        String cgpa = "";
        String intake = "";
        String facultyCode = "";
        String designation = "";

        if (sessionManager.isStudent()) {
            studentId = etStudentId.getText().toString().trim();
            semester = etSemester.getText().toString().trim();
            cgpa = etCgpa.getText().toString().trim();
            intake = etIntake.getText().toString().trim();
        }

        if (sessionManager.isFaculty()) {
            facultyCode = etFacultyCode.getText().toString().trim();
            designation = etDesignation.getText().toString().trim();
        }

        showLoading(true);

        // Create update request
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setName(name);
        updateRequest.setEmail(email);
        updateRequest.setPhone(phone.isEmpty() ? null : phone);
        updateRequest.setDepartment(department.isEmpty() ? null : department);

        // Add user type specific fields
        if (sessionManager.isStudent()) {
            updateRequest.setStudentId(studentId.isEmpty() ? null : studentId);
            updateRequest.setSemester(semester.isEmpty() ? null : semester);
            updateRequest.setCgpa(cgpa.isEmpty() ? null : cgpa);
            updateRequest.setIntake(intake.isEmpty() ? null : intake);
        }

        if (sessionManager.isFaculty()) {
            updateRequest.setFacultyCode(facultyCode.isEmpty() ? null : facultyCode);
            updateRequest.setDesignation(designation.isEmpty() ? null : designation);
        }

        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            showLoading(false);
            redirectToLogin();
            return;
        }

        // Clean token (remove "Bearer " if already present)
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        Log.d(TAG, "Updating profile...");

        Call<ProfileUpdateResponse> call = apiService.updateProfile("Bearer " + cleanToken, updateRequest);

        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ProfileUpdateResponse updateResponse = response.body();

                    if (updateResponse.isSuccess()) {
                        // Update session with new data
                        updateSessionData(updateResponse);

                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String errorMsg = updateResponse.getMessage() != null ?
                                updateResponse.getMessage() : "Update failed";
                        Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API returned success: false - " + errorMsg);
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Profile update failed: " + t.getMessage(), t);
                Toast.makeText(EditProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return "";

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error converting bitmap to Base64: " + e.getMessage());
            return "";
        }
    }

    private void updateSessionData(ProfileUpdateResponse updateResponse) {
        if (updateResponse.getData() != null && updateResponse.getData().getUser() != null) {
            User user = updateResponse.getData().getUser();

            // Update session data
            sessionManager.saveUserData(
                    user.getName(),
                    user.getEmail(),
                    user.getUserType(),
                    user.isStudent(),
                    user.isFaculty()
            );

            // Update user details if available
            if (user.getDetails() != null) {
                sessionManager.saveUserDetails(user.getDetails());
            }
        }
    }

    private void handleErrorResponse(Response<ProfileUpdateResponse> response) {
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
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error Body: " + errorBody);
                Toast.makeText(this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reading error body: " + e.getMessage());
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
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
        btnCancel.setEnabled(!show);
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