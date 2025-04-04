package com.example.desarrollo_aplicaciones.api.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    @SerializedName("email")
    private String email;

    @SerializedName("uid")
    private String uid;
    
    @SerializedName("token")
    private String token;

    // Constructor vacío
    public AuthResponse() {
    }

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}