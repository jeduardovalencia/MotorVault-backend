package com.motorvault.backend.controller;

import com.motorvault.backend.dto.request.LoginRequest;
import com.motorvault.backend.dto.request.RegisterRequest;
import com.motorvault.backend.dto.response.ApiResponse;
import com.motorvault.backend.dto.response.AuthResponse;
import com.motorvault.backend.entity.User;
import com.motorvault.backend.repository.UserRepository;
import com.motorvault.backend.service.ActivityLogService;
import com.motorvault.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                           HttpServletRequest httpRequest) {
        AuthResponse response = authService.login(request);

        userRepository.findByEmail(request.getEmail()).ifPresent(user ->
                activityLogService.registrar(
                        "LOGIN",
                        "Login exitoso: " + user.getEmail(),
                        user.getId(),
                        user.getEmail(),
                        httpRequest));

        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Registration successful", authService.register(request));
    }
}
