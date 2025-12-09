package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.octosync.bubtnexus.models.ListResponse;
import com.octosync.bubtnexus.models.LoginResponse;
import com.octosync.bubtnexus.models.Program;
import com.octosync.bubtnexus.models.ProgramsResponse;
import com.octosync.bubtnexus.models.RegisterRequest;
import com.octosync.bubtnexus.models.SemesterOptionsResponse;
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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    // Common fields
    private TextInputEditText editTextName, editTextEmail, editTextPhone, editTextPassword, editTextConfirmPassword;

    // Student fields
    private TextInputEditText editTextStudentId, editTextIntake, editTextSection, editTextCgpa;
    private MaterialAutoCompleteTextView autoCompleteSemester, autoCompleteProgram;

    // Faculty fields
    private TextInputEditText editTextFacultyCode;
    private MaterialAutoCompleteTextView autoCompleteDepartment, autoCompleteDesignation;

    // Layouts
    private TabLayout tabLayout;
    private Button buttonRegister;
    private ProgressBar progressBar;
    private TextView textViewLogin, textViewError;
    private View studentFieldsLayout, facultyFieldsLayout;
    private SessionManager sessionManager;

    // Data for dropdowns
    private List<Program> programList = new ArrayList<>();
    private Map<String, String> semesterMap = new HashMap<>();
    private List<String> departmentList = new ArrayList<>();
    private List<String> designationList = new ArrayList<>();

    // Selected values
    private Long selectedProgramId = null;
    private String selectedSemesterValue = null;
    private String selectedDepartment = null;
    private String selectedDesignation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        initializeViews();
        setupTabListener();
        setupListeners();

        // Load dropdown data
        loadDropdownData();
    }

    private void initializeViews() {
        // Common fields
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        // Student fields
        editTextStudentId = findViewById(R.id.editTextStudentId);
        editTextIntake = findViewById(R.id.editTextIntake);
        editTextSection = findViewById(R.id.editTextSection);
        editTextCgpa = findViewById(R.id.editTextCgpa);
        autoCompleteSemester = findViewById(R.id.autoCompleteSemester);
        autoCompleteProgram = findViewById(R.id.autoCompleteProgram);

        // Faculty fields
        editTextFacultyCode = findViewById(R.id.editTextFacultyCode);
        autoCompleteDepartment = findViewById(R.id.autoCompleteDepartment);
        autoCompleteDesignation = findViewById(R.id.autoCompleteDesignation);

        // Layouts
        studentFieldsLayout = findViewById(R.id.studentFieldsLayout);
        facultyFieldsLayout = findViewById(R.id.facultyFieldsLayout);

        // Other views
        tabLayout = findViewById(R.id.tabLayout);
        buttonRegister = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewError = findViewById(R.id.textViewError);

        progressBar.setVisibility(View.GONE);
        textViewError.setVisibility(View.GONE);
    }

    private void setupTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Student
                        studentFieldsLayout.setVisibility(View.VISIBLE);
                        facultyFieldsLayout.setVisibility(View.GONE);
                        break;
                    case 1: // Faculty
                        studentFieldsLayout.setVisibility(View.GONE);
                        facultyFieldsLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupListeners() {
        buttonRegister.setOnClickListener(v -> registerUser());

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadDropdownData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Load programs
        apiService.getPrograms().enqueue(new Callback<ProgramsResponse>() {
            @Override
            public void onResponse(Call<ProgramsResponse> call, Response<ProgramsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    programList = response.body().getData();
                    setupProgramAutoComplete();
                }
            }

            @Override
            public void onFailure(Call<ProgramsResponse> call, Throwable t) {
                Log.e(TAG, "Failed to load programs: " + t.getMessage());
            }
        });

        // Load semesters
        apiService.getSemesterOptions().enqueue(new Callback<SemesterOptionsResponse>() {
            @Override
            public void onResponse(Call<SemesterOptionsResponse> call, Response<SemesterOptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    semesterMap = response.body().getSemesterMap();
                    setupSemesterAutoComplete();
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

    private void setupProgramAutoComplete() {
        List<String> programNames = new ArrayList<>();
        programNames.add("Select Program");

        for (Program program : programList) {
            programNames.add(program.getName() + " (" + program.getCode() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, programNames);

        autoCompleteProgram.setAdapter(adapter);

        autoCompleteProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && programList.size() > position - 1) {
                    selectedProgramId = (long) programList.get(position - 1).getId();
                } else {
                    selectedProgramId = null;
                }
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

        autoCompleteSemester.setAdapter(adapter);

        autoCompleteSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        autoCompleteDepartment.setAdapter(adapter);

        autoCompleteDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        autoCompleteDesignation.setAdapter(adapter);

        autoCompleteDesignation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Common validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill all required fields");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Invalid email address");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        int selectedTab = tabLayout.getSelectedTabPosition();
        RegisterRequest registerRequest;

        if (selectedTab == 0) { // Student
            String studentId = editTextStudentId.getText().toString().trim();
            String intakeStr = editTextIntake.getText().toString().trim();
            String sectionStr = editTextSection.getText().toString().trim();
            String cgpaStr = editTextCgpa.getText().toString().trim();

            // Get selected values from AutoCompleteTextViews
            String programText = autoCompleteProgram.getText().toString().trim();
            String semesterText = autoCompleteSemester.getText().toString().trim();

            // Student specific validation
            if (studentId.isEmpty() || intakeStr.isEmpty() || sectionStr.isEmpty() ||
                    semesterText.isEmpty() || programText.isEmpty() ||
                    semesterText.equals("Select Semester") || programText.equals("Select Program")) {
                showError("Please fill all student fields");
                return;
            }

            try {
                Integer intake = Integer.parseInt(intakeStr);
                Integer section = Integer.parseInt(sectionStr);
                Double cgpa = cgpaStr.isEmpty() ? null : Double.parseDouble(cgpaStr);

                // Find program ID
                for (Program program : programList) {
                    String programDisplayName = program.getName() + " (" + program.getCode() + ")";
                    if (programDisplayName.equals(programText)) {
                        selectedProgramId = (long) program.getId();
                        break;
                    }
                }

                // Find semester value
                if (semesterMap.containsKey(semesterText)) {
                    selectedSemesterValue = semesterMap.get(semesterText);
                } else {
                    selectedSemesterValue = semesterText;
                }

                registerRequest = new RegisterRequest(name, email, password, confirmPassword,
                        phone, studentId, selectedProgramId, selectedSemesterValue,
                        intake, section, cgpa);

            } catch (NumberFormatException e) {
                showError("Please enter valid numbers for intake, section, and CGPA");
                return;
            }

        } else { // Faculty
            String facultyCode = editTextFacultyCode.getText().toString().trim();
            String departmentText = autoCompleteDepartment.getText().toString().trim();
            String designationText = autoCompleteDesignation.getText().toString().trim();

            // Faculty specific validation
            if (facultyCode.isEmpty() || departmentText.isEmpty() || designationText.isEmpty() ||
                    departmentText.equals("Select Department") || designationText.equals("Select Designation")) {
                showError("Please fill all faculty fields");
                return;
            }

            selectedDepartment = departmentText;
            selectedDesignation = designationText;

            registerRequest = new RegisterRequest(name, email, password, confirmPassword,
                    phone, facultyCode, selectedDepartment, selectedDesignation);
        }

        // Proceed with API call
        progressBar.setVisibility(View.VISIBLE);
        buttonRegister.setEnabled(false);
        textViewError.setVisibility(View.GONE);

        autoCompleteSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedText = (String) parent.getItemAtPosition(position);
                    Log.d(TAG, "User selected semester text: " + selectedText);

                    if (semesterMap.containsKey(selectedText)) {
                        selectedSemesterValue = semesterMap.get(selectedText);
                        Log.d(TAG, "Mapped to semester value: " + selectedSemesterValue);
                    } else {
                        selectedSemesterValue = selectedText;
                        Log.d(TAG, "Using text as semester value: " + selectedSemesterValue);
                    }
                } else {
                    selectedSemesterValue = null;
                    Log.d(TAG, "Select Semester placeholder selected");
                }
            }
        });
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.register(registerRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                buttonRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse registerResponse = response.body();

                    if (registerResponse.isSuccess() && registerResponse.getData() != null) {
                        LoginResponse.Data data = registerResponse.getData();
                        String token = data.getTokenType() + " " + data.getAccessToken();
                        LoginResponse.User user = data.getUser();

                        sessionManager.saveToken(token);
                        sessionManager.saveUserId(user.getId());
                        sessionManager.saveUserData(
                                user.getName(),
                                user.getEmail(),
                                user.getUserType(),
                                user.isStudent(),
                                user.isFaculty()
                        );

                        if (user.getDetails() != null) {
                            sessionManager.saveUserDetails(user.getDetails());
                        }

                        if (user.getRoles() != null) {
                            sessionManager.saveUserRoles(user.getRoles());
                        }

                        Toast.makeText(RegisterActivity.this,
                                "Registration successful! Welcome " + user.getName(),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String errorMessage = registerResponse.getMessage() != null ?
                                registerResponse.getMessage() : "Registration failed";
                        showError(errorMessage);
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (errorBody.contains("email")) {
                                showError("Email already exists");
                            } else {
                                showError("Error: " + response.code());
                            }
                        } else {
                            showError("Error: " + response.code());
                        }
                    } catch (Exception e) {
                        showError("Registration failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonRegister.setEnabled(true);
                Log.e(TAG, "Network error: " + t.getMessage());

                if (t.getMessage().contains("malformed JSON") || t.getMessage().contains("JsonReader")) {
                    showError("Server returned invalid response (HTML instead of JSON). Please check API endpoint.");
                } else {
                    showError("Network error: " + t.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        textViewError.setText(message);
        textViewError.setVisibility(View.VISIBLE);
    }
}