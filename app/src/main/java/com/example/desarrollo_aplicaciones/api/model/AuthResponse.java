package com.example.desarrollo_aplicaciones.api.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("name")
    private String name;

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}