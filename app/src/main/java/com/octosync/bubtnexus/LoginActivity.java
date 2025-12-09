package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.octosync.bubtnexus.models.LoginRequest;
import com.octosync.bubtnexus.models.LoginResponse;
import com.octosync.bubtnexus.network.ApiClient;
import com.octosync.bubtnexus.network.ApiService;
import com.octosync.bubtnexus.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private TextView textViewError, textViewRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            redirectToMainActivity();
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);
        textViewRegister = findViewById(R.id.textViewRegister);
    }

    private void setupListeners() {
        buttonLogin.setOnClickListener(v -> loginUser());

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        textViewError.setVisibility(View.GONE);

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess() && loginResponse.getData() != null) {
                        LoginResponse.Data data = loginResponse.getData();

                        // Construct token
                        String token = data.getTokenType() + " " + data.getAccessToken();

                        // Get user from data
                        LoginResponse.User user = data.getUser();

                        // Save user data to session
                        sessionManager.saveToken(token);
                        sessionManager.saveUserId(user.getId());
                        sessionManager.saveUserData(
                                user.getName(),
                                user.getEmail(),
                                user.getUserType(),
                                user.isStudent(),
                                user.isFaculty()
                        );

                        // Save user details if available
                        if (user.getDetails() != null) {
                            sessionManager.saveUserDetails(user.getDetails());

                            // Log program information for debugging
                            Log.d(TAG, "User Details Found - Saving to Session");
                            if (user.getDetails().getProgram() != null) {
                                Log.d(TAG, "Program ID: " + user.getDetails().getProgram().getId());
                                Log.d(TAG, "Program Name: " + user.getDetails().getProgram().getName());
                                Log.d(TAG, "Program Code: " + user.getDetails().getProgram().getCode());
                            }
                        } else {
                            Log.d(TAG, "User Details is null!");
                        }

                        // Save user roles if available
                        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                            sessionManager.saveUserRoles(user.getRoles());
                        }

                        // Debug log to verify all data is saved
                        Log.d(TAG, "=== After Login - Session Data ===");
                        Log.d(TAG, "Token: " + (sessionManager.getToken() != null ? "Exists" : "Null"));
                        Log.d(TAG, "User ID: " + sessionManager.getUserId());
                        Log.d(TAG, "User Name: " + sessionManager.getUserName());
                        Log.d(TAG, "User Email: " + sessionManager.getUserEmail());
                        Log.d(TAG, "User Type: " + sessionManager.getUserType());
                        Log.d(TAG, "Is Student: " + sessionManager.isStudent());
                        Log.d(TAG, "Is Faculty: " + sessionManager.isFaculty());
                        Log.d(TAG, "Student ID: " + sessionManager.getStudentId());
                        Log.d(TAG, "Semester: " + sessionManager.getSemester());
                        Log.d(TAG, "Program Code: " + sessionManager.getProgramCode());
                        Log.d(TAG, "Program Name: " + sessionManager.getProgramName());
                        Log.d(TAG, "Intake: " + sessionManager.getIntake());
                        Log.d(TAG, "Section: " + sessionManager.getSection());
                        Log.d(TAG, "CGPA: " + sessionManager.getCgpa());

                        Toast.makeText(LoginActivity.this,
                                "Welcome " + user.getName(),
                                Toast.LENGTH_SHORT).show();

                        redirectToMainActivity();

                    } else {
                        String errorMessage = loginResponse.getMessage() != null ?
                                loginResponse.getMessage() : "Login failed";
                        showError(errorMessage);
                    }
                } else {
                    // Handle HTTP error responses
                    try {
                        if (response.code() == 401) {
                            showError("Invalid email or password");
                        } else if (response.code() == 422) {
                            showError("Validation error. Please check your input.");
                        } else if (response.code() == 500) {
                            showError("Server error. Please try again later.");
                        } else {
                            showError("Login failed. Error code: " + response.code());
                        }
                    } catch (Exception e) {
                        showError("Login failed. Please try again.");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonLogin.setEnabled(true);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        textViewError.setText(message);
        textViewError.setVisibility(View.VISIBLE);
    }
}