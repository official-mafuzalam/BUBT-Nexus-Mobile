package com.octosync.bubtnexus.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "BubtNexusPrefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PROGRAM = "program";
    private static final String KEY_SEMESTER = "semester";
    private static final String KEY_INTAKE = "intake";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SharedPreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUserProgramInfo(String program, String semester, String intake) {
        editor.putString(KEY_PROGRAM, program);
        editor.putString(KEY_SEMESTER, semester);
        editor.putString(KEY_INTAKE, intake);
        editor.apply();
    }

    public String getProgram() {
        return prefs.getString(KEY_PROGRAM, "006"); // default value
    }

    public String getSemester() {
        return prefs.getString(KEY_SEMESTER, "611"); // default value
    }

    public String getIntake() {
        return prefs.getString(KEY_INTAKE, "50 - 1"); // default value
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}