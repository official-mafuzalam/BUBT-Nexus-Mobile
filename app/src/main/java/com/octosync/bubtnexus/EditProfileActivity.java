package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.octosync.bubtnexus.models.ListResponse;
import com.octosync.bubtnexus.models.ProfileUpdateRequest;
import com.octosync.bubtnexus.models.ProfileUpdateResponse;
import com.octosync.bubtnexus.models.SemesterOptionsResponse;
import com.octosync.bubtnexus.models.User;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private EditText etName, etEmail, etPhone, etSection, etFacultyCode, etIntake, etCgpa;
    private MaterialAutoCompleteTextView etSemester, etDepartment, etDesignation;
    private ImageView ivProfile;
    private MaterialButton btnSave, btnCancel;
    private ImageButton btnBack;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;

    // Data for dropdowns
    private Map<String, String> semesterMap = new HashMap<>();
    private List<String> departmentList = new ArrayList<>();
    private List<String> designationList = new ArrayList<>();

    // Selected values
    private String selectedSemesterValue = null;
    private String selectedDepartment = null;
    private String selectedDesignation = null;

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
        loadDropdownData();
    }

    private void initializeViews() {
        // EditTexts
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etSection = findViewById(R.id.etSection);
        etFacultyCode = findViewById(R.id.etFacultyCode);
        etIntake = findViewById(R.id.etIntake);
        etCgpa = findViewById(R.id.etCgpa);

        // MaterialAutoCompleteTextViews
        etSemester = findViewById(R.id.etSemester);
        etDepartment = findViewById(R.id.etDepartment);
        etDesignation = findViewById(R.id.etDesignation);

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

        // Find layouts
        View intakeLayout = findViewById(R.id.intakeLayout);
        View sectionLayout = findViewById(R.id.sectionLayout);
        View cgpaLayout = findViewById(R.id.cgpaLayout);
        View semesterLayout = findViewById(R.id.semesterLayout);
        View departmentLayout = findViewById(R.id.departmentLayout);
        View facultyCodeLayout = findViewById(R.id.facultyCodeLayout);
        View designationLayout = findViewById(R.id.designationLayout);

        // Show/hide student fields
        if (intakeLayout != null) {
            intakeLayout.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (sectionLayout != null) {
            sectionLayout.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (cgpaLayout != null) {
            cgpaLayout.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }
        if (semesterLayout != null) {
            semesterLayout.setVisibility(isStudent ? View.VISIBLE : View.GONE);
        }

        // Show/hide faculty fields
        if (departmentLayout != null) {
            departmentLayout.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
        if (facultyCodeLayout != null) {
            facultyCodeLayout.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
        if (designationLayout != null) {
            designationLayout.setVisibility(isFaculty ? View.VISIBLE : View.GONE);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        ivProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Image upload system not yet implemented", Toast.LENGTH_SHORT).show();
        });
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadCurrentUserData() {
        try {
            // Load current user data into form
            etName.setText(sessionManager.getUserName());
            etEmail.setText(sessionManager.getUserEmail());
            etPhone.setText(sessionManager.getPhone() != null ? sessionManager.getPhone() : "");

            // Load user type specific data
            if (sessionManager.isStudent()) {
                String sectionValue = sessionManager.getSection() > 0
                        ? String.valueOf(sessionManager.getSection())
                        : "";
                etSection.setText(sectionValue);

                String semester = sessionManager.getSemester();
                if (semester != null && !semester.isEmpty()) {
                    etSemester.setText(semester);
                }

                etCgpa.setText(sessionManager.getCgpa() != null ? sessionManager.getCgpa() : "");

                int intake = sessionManager.getIntake();
                etIntake.setText(intake > 0 ? String.valueOf(intake) : "");
            }

            if (sessionManager.isFaculty()) {
                etFacultyCode.setText(sessionManager.getFacultyCode() != null ? sessionManager.getFacultyCode() : "");

                String department = sessionManager.getDepartment();
                if (department != null && !department.isEmpty()) {
                    etDepartment.setText(department);
                }

                String designation = sessionManager.getDesignation();
                if (designation != null && !designation.isEmpty()) {
                    etDesignation.setText(designation);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage());
            Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDropdownData() {
        // Load semesters
        apiService.getSemesterOptions().enqueue(new Callback<SemesterOptionsResponse>() {
            @Override
            public void onResponse(Call<SemesterOptionsResponse> call, Response<SemesterOptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    semesterMap = response.body().getSemesterMap();
                    setupSemesterAutoComplete();

                    // Set current semester if exists
                    String currentSemester = sessionManager.getSemester();
                    if (currentSemester != null && !currentSemester.isEmpty()) {
                        // Find semester name from code
                        for (Map.Entry<String, String> entry : semesterMap.entrySet()) {
                            if (entry.getValue().equals(currentSemester)) {
                                etSemester.setText(entry.getKey());
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SemesterOptionsResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load semesters: " + t.getMessage());
            }
        });

        // Load departments
        apiService.getDepartments().enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    departmentList = response.body().getData();
                    setupDepartmentAutoComplete();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load departments: " + t.getMessage());
            }
        });

        // Load designations
        apiService.getDesignations().enqueue(new Callback<ListResponse>() {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    designationList = response.body().getData();
                    setupDesignationAutoComplete();
                }
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load designations: " + t.getMessage());
            }
        });
    }

    private void setupSemesterAutoComplete() {
        List<String> semesterOptions = new ArrayList<>();
        semesterOptions.add("Select Semester");

        if (!semesterMap.isEmpty()) {
            semesterOptions.addAll(semesterMap.keySet());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, semesterOptions);

        etSemester.setAdapter(adapter);

        etSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedText = (String) parent.getItemAtPosition(position);
                    if (semesterMap.containsKey(selectedText)) {
                        selectedSemesterValue = semesterMap.get(selectedText);
                    } else {
                        selectedSemesterValue = selectedText;
                    }
                } else {
                    selectedSemesterValue = null;
                }
            }
        });
    }

    private void setupDepartmentAutoComplete() {
        List<String> departments = new ArrayList<>();
        departments.add("Select Department");
        departments.addAll(departmentList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, departments);

        etDepartment.setAdapter(adapter);

        etDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedDepartment = (String) parent.getItemAtPosition(position);
                } else {
                    selectedDepartment = null;
                }
            }
        });
    }

    private void setupDesignationAutoComplete() {
        List<String> designations = new ArrayList<>();
        designations.add("Select Designation");
        designations.addAll(designationList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, designations);

        etDesignation.setAdapter(adapter);

        etDesignation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedDesignation = (String) parent.getItemAtPosition(position);
                } else {
                    selectedDesignation = null;
                }
            }
        });
    }

    private void updateProfile() {
        // Get form values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

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
        String studentSection = "";
        String semester = "";
        String cgpa = "";
        String intake = "";
        String facultyCode = "";
        String department = "";
        String designation = "";

        if (sessionManager.isStudent()) {
            studentSection = etSection.getText().toString().trim();
            semester = etSemester.getText().toString().trim();
            cgpa = etCgpa.getText().toString().trim();
            intake = etIntake.getText().toString().trim();
        }

        if (sessionManager.isFaculty()) {
            facultyCode = etFacultyCode.getText().toString().trim();
            department = etDepartment.getText().toString().trim();
            designation = etDesignation.getText().toString().trim();
        }

        showLoading(true);

        // Create update request
        ProfileUpdateRequest updateRequest = new ProfileUpdateRequest();
        updateRequest.setName(name);
        updateRequest.setEmail(email);
        updateRequest.setPhone(phone.isEmpty() ? null : phone);

        // Add user type specific fields
        if (sessionManager.isStudent()) {
            updateRequest.setSection(studentSection.isEmpty() ? null : studentSection);

            // Get semester value from map
            if (!semester.isEmpty() && !semester.equals("Select Semester")) {
                if (semesterMap.containsKey(semester)) {
                    updateRequest.setSemester(semesterMap.get(semester));
                } else {
                    updateRequest.setSemester(semester);
                }
            }

            updateRequest.setCgpa(cgpa.isEmpty() ? null : cgpa);
            updateRequest.setIntake(intake.isEmpty() ? null : intake);
        }

        if (sessionManager.isFaculty()) {
            updateRequest.setFacultyCode(facultyCode.isEmpty() ? null : facultyCode);
            updateRequest.setDepartment(department.isEmpty() || department.equals("Select Department") ? null : department);
            updateRequest.setDesignation(designation.isEmpty() || designation.equals("Select Designation") ? null : designation);
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
                User.UserDetails details = user.getDetails();

                // Save individual detail fields
                sessionManager.saveUserDetails(
                        details.getPhone(),
                        details.getStudentId(),
                        details.getFacultyCode(),
                        details.getDepartment(),
                        details.getDesignation(),
                        details.getSemester(),
                        details.getIntake(),
                        details.getSection(),
                        details.getCgpa(),
                        details.getProfilePicture()
                );

                // Save program info if available
                if (details.getProgram() != null) {
                    sessionManager.saveProgramInfo(
                            details.getProgram().getId(),
                            details.getProgram().getName(),
                            details.getProgram().getCode()
                    );
                }
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
        // Clean up resources if needed
    }
}