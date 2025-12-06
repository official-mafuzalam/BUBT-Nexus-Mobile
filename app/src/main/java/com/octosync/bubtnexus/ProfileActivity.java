package com.octosync.bubtnexus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.octosync.bubtnexus.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;
    private TextView tvUserName, tvStudentId, tvDepartment, tvEmail, tvPhone, tvSemester, tvBatch, tvDate, tvDesignation;
    private MaterialButton btnEditProfile, btnSettings;
    private ImageButton btnBack;
    private ImageView ivProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setCurrentDate();
        loadUserData();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.getToken() != null;
    }

    private void initializeViews() {
        // TextViews
        tvUserName = findViewById(R.id.tvUserName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvSemester = findViewById(R.id.tvSemester);
        tvBatch = findViewById(R.id.tvBatch);
        tvDate = findViewById(R.id.tvDate);
        tvDesignation = findViewById(R.id.tvDesignation); // Add this if exists

        // ImageView
        ivProfile = findViewById(R.id.ivProfile);

        // Buttons
        btnBack = findViewById(R.id.btnBack);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSettings = findViewById(R.id.btnSettings);

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupClickListeners() {
        Log.d(TAG, "setupClickListeners: Setting up click listeners");

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> navigateToMainActivity());
        }

        btnEditProfile.setOnClickListener(v -> navigateToEditProfile());

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> showToast("Settings clicked"));
        }
    }

    private void setupBottomNavigation() {
        try {
            // Set profile as selected by default
            bottomNavigation.setSelectedItemId(R.id.nav_profile);

            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    navigateToMainActivity();
                    return true;
                } else if (id == R.id.nav_task) {
                    navigateToTaskActivity();
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Already on profile page
                    return true;
                } else if (id == R.id.nav_logout) {
                    logoutUser();
                    return true;
                }
                return false;
            });
        } catch (Exception e) {
            Log.e(TAG, "setupBottomNavigation: Error", e);
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToTaskActivity() {
        Intent intent = new Intent(ProfileActivity.this, TaskActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Refresh user data when returning from edit profile
            loadUserData();
            showToast("Profile updated successfully");
        }
    }

    private void logoutUser() {
        sessionManager.clear();
        showToast("Logged out successfully");
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadUserData() {
        try {
            // Get user data from session
            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getUserEmail();
            String studentId = sessionManager.getStudentId();
            String department = sessionManager.getDepartment();
            String phone = sessionManager.getPhone();
            String semester = sessionManager.getSemester();
            String program = sessionManager.getProgram();
            String facultyCode = sessionManager.getFacultyCode(); // Changed from getFacultyId()
            String designation = sessionManager.getDesignation();
            String intake = String.valueOf(sessionManager.getIntake()); // Changed to String
            String cgpa = sessionManager.getCgpa();
            int section = sessionManager.getSection();

            String userType = sessionManager.getUserType();
            boolean isStudent = sessionManager.isStudent();
            boolean isFaculty = sessionManager.isFaculty();

            // Set basic user info
            if (tvUserName != null && userName != null) {
                tvUserName.setText(userName);
            }

            if (tvEmail != null && userEmail != null) {
                tvEmail.setText(userEmail);
            }

            // Display user type specific information
            if (isStudent) {
                // Student specific data
                if (tvStudentId != null) {
                    String displayStudentId = studentId != null ? studentId : "Not assigned";
                    tvStudentId.setText(displayStudentId);
                }

                if (tvSemester != null) {
                    String displaySemester = semester != null ? semester + " Semester" : "Not specified";
                    tvSemester.setText(displaySemester);
                }

                if (tvBatch != null) {
                    String batchText = "Intake: " + intake + ", Section: " + section;
                    if (cgpa != null) {
                        batchText += ", CGPA: " + cgpa;
                    }
                    tvBatch.setText(batchText);
                }

                if (tvDesignation != null) {
                    tvDesignation.setText("Student");
                    tvDesignation.setVisibility(View.VISIBLE);
                }
            } else if (isFaculty) {
                // Faculty specific data
                if (tvStudentId != null) {
                    String displayFacultyCode = facultyCode != null ? facultyCode : "Not assigned";
                    tvStudentId.setText("Faculty Code: " + displayFacultyCode);
                }

                if (tvSemester != null) {
                    String displaySemester = designation != null ? designation : "Faculty Member";
                    tvSemester.setText(displaySemester);
                }

                if (tvBatch != null) {
                    tvBatch.setText("Faculty");
                }

                if (tvDesignation != null) {
                    tvDesignation.setText(designation != null ? designation : "Faculty");
                    tvDesignation.setVisibility(View.VISIBLE);
                }
            } else {
                // Other user types (admin, etc.)
                if (tvStudentId != null) {
                    tvStudentId.setText("User ID: " + sessionManager.getUserId());
                }

                if (tvSemester != null) {
                    tvSemester.setText("User Type: " + userType);
                }

                if (tvBatch != null) {
                    tvBatch.setText("System User");
                }

                if (tvDesignation != null) {
                    tvDesignation.setText(userType);
                    tvDesignation.setVisibility(View.VISIBLE);
                }
            }

            // Common data for all users
            if (tvDepartment != null) {
                String displayDepartment = department != null ? department :
                        (program != null ? program : "Not specified");
                tvDepartment.setText(displayDepartment);
            }

            if (tvPhone != null) {
                String displayPhone = phone != null ? phone : "Not provided";
                tvPhone.setText(displayPhone);
            }

            // Load profile picture
            loadProfilePicture();
        } catch (Exception e) {
            Log.e(TAG, "loadUserData: Error", e);
        }
    }

    private void loadProfilePicture() {
        try {
            // Get profile picture from session manager
            String profilePicturePath = sessionManager.getProfilePicture();

            if (profilePicturePath != null && !profilePicturePath.isEmpty() && ivProfile != null) {
                // If it's a Base64 string (for local storage)
                if (profilePicturePath.startsWith("data:image") || profilePicturePath.startsWith("/9j/")) {
                    try {
                        // Extract base64 part if it's a data URL
                        String base64Data = profilePicturePath;
                        if (profilePicturePath.contains(",")) {
                            base64Data = profilePicturePath.substring(profilePicturePath.indexOf(",") + 1);
                        }

                        byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        ivProfile.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e(TAG, "Error decoding Base64 profile picture: " + e.getMessage());
                        ivProfile.setImageResource(R.drawable.ic_person);
                    }
                } else {
                    // If it's a URL path, you would need to load it with Glide/Picasso
                    // For now, show default image
                    ivProfile.setImageResource(R.drawable.ic_person);
                }
            } else {
                // Set default profile image
                ivProfile.setImageResource(R.drawable.ic_person);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadProfilePicture: Error", e);
            ivProfile.setImageResource(R.drawable.ic_person);
        }
    }

    private void setCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            if (tvDate != null) {
                tvDate.setText(currentDate);
            }
        } catch (Exception e) {
            Log.e(TAG, "setCurrentDate: Error", e);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToMainActivity();
    }
}