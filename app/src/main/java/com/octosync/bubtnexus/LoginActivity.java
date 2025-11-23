package com.octosync.bubtnexus;

import android.content.Intent;
import android.os.Bundle;
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
                        String token = loginResponse.getData().getTokenType() + " " + loginResponse.getData().getAccessToken();
                        LoginResponse.User user = loginResponse.getData().getUser();

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
                        }

                        if (user.getRoles() != null) {
                            sessionManager.saveUserRoles(user.getRoles());
                        }

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
                    switch (response.code()) {
                        case 401:
                            showError("Invalid email or password");
                            break;
                        case 422:
                            showError("Validation error. Please check your input.");
                            break;
                        case 500:
                            showError("Server error. Please try again later.");
                            break;
                        default:
                            showError("Login failed. Please try again.");
                            break;
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