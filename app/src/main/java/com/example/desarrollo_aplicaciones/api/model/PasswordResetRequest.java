package com.example.desarrollo_aplicaciones.api.model;

public class PasswordResetRequest {
    private String token;
    private String newPassword;

    public PasswordResetRequest() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}