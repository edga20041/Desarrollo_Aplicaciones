package com.example.desarrollo_aplicaciones.api.model;
import com.google.gson.annotations.SerializedName;

public class RecoverPasswordRequest {

    @SerializedName("email")
    private String email;

    public RecoverPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
