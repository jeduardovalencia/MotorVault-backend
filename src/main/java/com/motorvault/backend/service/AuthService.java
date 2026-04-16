package com.motorvault.backend.service;

import com.motorvault.backend.dto.request.LoginRequest;
import com.motorvault.backend.dto.request.RegisterRequest;
import com.motorvault.backend.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}
