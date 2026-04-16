package com.motorvault.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleFilterRequest {

    private String placa;
    private String modelo;
    private String marca;
    private Integer anio;
}
