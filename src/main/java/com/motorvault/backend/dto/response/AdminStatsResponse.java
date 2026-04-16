package com.motorvault.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class AdminStatsResponse {

    private long totalUsuarios;
    private long usuariosActivos;
    private long totalVehiculos;
    private Map<String, Long> vehiculosPorMarca;
}
