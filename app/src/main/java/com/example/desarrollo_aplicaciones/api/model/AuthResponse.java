package com.example.desarrollo_aplicaciones.api.model;
import com.example.desarrollo_aplicaciones.entity.User;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("user")
    private User user;

    // Constructor vacío
    public AuthResponse() {
    }

    // Getter
    public User getUser() {
        return user;
    }

    // Setter
    public void setUser(User user) {
        this.user = user;
    }
}