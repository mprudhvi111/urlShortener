package com.urlShortener.urlShortener.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserForm(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message="Name is required")
        String name,
        @NotBlank(message = "Password is required")
        String password) {
}
