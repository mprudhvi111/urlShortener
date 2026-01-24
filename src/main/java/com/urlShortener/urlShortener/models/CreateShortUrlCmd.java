package com.urlShortener.urlShortener.models;

public record CreateShortUrlCmd (
    String originalUrl,
    Boolean isPrivate,
    Integer expirationInDays,
    Long userId){
}
