package com.motorvault.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VehicleResponse {

    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer anio;
    private String color;
    private String fotoUrl;
    private String usuarioNombre;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
