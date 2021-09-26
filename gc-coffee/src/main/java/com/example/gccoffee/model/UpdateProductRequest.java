package com.example.gccoffee.model;

public class UpdateProductRequest {
    private String description;

    public UpdateProductRequest() {
    }

    public UpdateProductRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
