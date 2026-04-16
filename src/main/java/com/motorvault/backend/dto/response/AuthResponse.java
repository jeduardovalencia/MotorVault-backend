package com.motorvault.backend.dto.response;

import com.motorvault.backend.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private final String accessToken;

    @Builder.Default
    private final String tokenType = "Bearer";

    private final String email;
    private final String firstName;
    private final String lastName;
    private final Role role;
}
