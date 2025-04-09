package com.example.desarrollo_aplicaciones.api.model;

public class VerifyRequest {
    private String code;

    public VerifyRequest(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
