package com.motorvault.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 10, message = "La placa no puede superar 10 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Placa con formato inválido")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 50, message = "La marca no puede superar 50 caracteres")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 50, message = "El modelo no puede superar 50 caracteres")
    private String modelo;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 1900, message = "El año mínimo es 1900")
    @Max(value = 2030, message = "El año máximo es 2030")
    private Integer anio;

    @Size(max = 30, message = "El color no puede superar 30 caracteres")
    private String color;

    @Size(max = 5000000, message = "La URL de la foto no puede superar 5000000 caracteres")
    private String fotoUrl;
}
