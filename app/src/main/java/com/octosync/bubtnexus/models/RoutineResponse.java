package com.octosync.bubtnexus.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RoutineResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("program")
    private String program;

    @SerializedName("intake")
    private String intake;

    @SerializedName("semester")
    private String semester;

    @SerializedName("routine")
    private List<RoutineDay> routine;

    // Getters and Setters
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<RoutineDay> getRoutine() {
        return routine;
    }

    public void setRoutine(List<RoutineDay> routine) {
        this.routine = routine;
    }
}