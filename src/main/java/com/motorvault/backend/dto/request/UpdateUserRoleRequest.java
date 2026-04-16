package com.motorvault.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRoleRequest {

    @NotNull(message = "El rol es obligatorio")
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN|ROLE_MANAGER",
             message = "Rol inválido. Use: ROLE_USER, ROLE_ADMIN o ROLE_MANAGER")
    private String role;
}
