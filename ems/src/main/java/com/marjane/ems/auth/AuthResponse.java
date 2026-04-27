package com.marjane.ems.auth;


public record AuthResponse(
    boolean success,
    String message,
    String token,
    String errorCode,
    String role
) {
    // Overloaded constructor for backward compatibility (without errorCode and role)
    public AuthResponse(boolean success, String message, String token) {
        this(success, message, token, null, null);
    }
    
    // Overloaded constructor without role
    public AuthResponse(boolean success, String message, String token, String errorCode) {
        this(success, message, token, errorCode, null);
    }
}
