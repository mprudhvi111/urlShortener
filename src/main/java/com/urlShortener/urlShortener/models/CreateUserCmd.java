package com.urlShortener.urlShortener.models;

public record CreateUserCmd(
        String email,
        String name,
        String password,
        Role role)
{
}
