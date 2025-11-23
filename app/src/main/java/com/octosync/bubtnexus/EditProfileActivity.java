package com.octosync.bubtnexus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.octosync.bubtnexus.models.LoginResponse;
import com.octosync.bubtnexus.models.ProfileUpdateRequest;
import com.octosync.bubtnexus.models.ProfileUpdateResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etEmail, etPhone, etStudentId, etDepartment, etSemester, etBatch, etAddress;
    private ImageView ivProfile;
    private MaterialButton btnSave, btnCancel;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;
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
    }

    private void initializeViews() {
        // EditTexts
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etStudentId = findViewById(R.id.etStudentId);
        etDepartment = findViewById(R.id.etDepartment);
        etSemester = findViewById(R.id.etSemester);
        etBatch = findViewById(R.id.etBatch);
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

        // Set batch from intake or use default
        String intake = sessionManager.getIntake();
        if (intake != null && intake.contains("-")) {
            String[] parts = intake.split("-");
            if (parts.length >= 2) {
                String year = parts[0].trim();
                etBatch.setText(year + "-" + (Integer.parseInt(year) + 1));
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            ivProfile.setImageURI(selectedImageUri);
        }
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String semester = etSemester.getText().toString().trim();
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
        updateRequest.setPhone(phone);
        updateRequest.setStudentId(studentId.isEmpty() ? null : studentId);
        updateRequest.setDepartment(department.isEmpty() ? null : department);
        updateRequest.setSemester(semester.isEmpty() ? null : semester);
        updateRequest.setAddress(address.isEmpty() ? null : address);

        String token = sessionManager.getToken();
        if (token == null) {
            showToast("Please login again");
            return;
        }

        Call<ProfileUpdateResponse> call;

        if (selectedImageUri != null) {
            // Update with image
            call = updateProfileWithImage(token, updateRequest);
        } else {
            // Update without image
            call = apiService.updateProfileWithoutImage("Bearer " + token, updateRequest);
        }

        call.enqueue(new Callback<ProfileUpdateResponse>() {
            @Override
            public void onResponse(Call<ProfileUpdateResponse> call, Response<ProfileUpdateResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ProfileUpdateResponse updateResponse = response.body();
                    if (updateResponse.isSuccess()) {
                        // Update session with new data
                        updateSessionData(updateResponse);
                        showToast("Profile updated successfully");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        showToast(updateResponse.getMessage());
                    }
                } else {
                    showToast("Failed to update profile: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Profile update failed: " + t.getMessage());
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private Call<ProfileUpdateResponse> updateProfileWithImage(String token, ProfileUpdateRequest updateRequest) {
        try {
            // Convert URI to file
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            File file = new File(getCacheDir(), "profile_picture.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            // Create multipart request
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getName());
            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getEmail());
            RequestBody phoneBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getPhone() != null ? updateRequest.getPhone() : "");
            RequestBody studentIdBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getStudentId() != null ? updateRequest.getStudentId() : "");
            RequestBody departmentBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getDepartment() != null ? updateRequest.getDepartment() : "");
            RequestBody semesterBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getSemester() != null ? updateRequest.getSemester() : "");
            RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), updateRequest.getAddress() != null ? updateRequest.getAddress() : "");

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "profile_picture",
                    file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file)
            );

            return apiService.updateProfile(
                    "Bearer " + token,
                    nameBody, emailBody, studentIdBody,
                    RequestBody.create(MediaType.parse("text/plain"), ""), // faculty_id
                    RequestBody.create(MediaType.parse("text/plain"), ""), // program
                    semesterBody,
                    RequestBody.create(MediaType.parse("text/plain"), ""), // intake
                    RequestBody.create(MediaType.parse("text/plain"), ""), // cgpa
                    departmentBody,
                    RequestBody.create(MediaType.parse("text/plain"), ""), // designation
                    RequestBody.create(MediaType.parse("text/plain"), ""), // office_room
                    RequestBody.create(MediaType.parse("text/plain"), ""), // office_hours
                    phoneBody,
                    addressBody,
                    RequestBody.create(MediaType.parse("text/plain"), ""), // date_of_birth
                    RequestBody.create(MediaType.parse("text/plain"), ""), // emergency_contact
                    imagePart
            );

        } catch (Exception e) {
            Log.e(TAG, "Error preparing image upload: " + e.getMessage());
            return apiService.updateProfileWithoutImage("Bearer " + token, updateRequest);
        }
    }

    private void updateSessionData(ProfileUpdateResponse response) {
        if (response.getData() != null && response.getData().getUser() != null) {
            LoginResponse.User user = response.getData().getUser();

            // Update basic user info
            sessionManager.updateUserName(user.getName());
            sessionManager.updateUserEmail(user.getEmail());

            // Update user details if available
            if (user.getDetails() != null) {
                sessionManager.saveUserDetails(user.getDetails());
            }
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}