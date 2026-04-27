package com.marjane.ems.auth;

public record LoginRequest(
    String eid,
    String password
) {}