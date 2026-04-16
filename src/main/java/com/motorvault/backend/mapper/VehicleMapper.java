package com.motorvault.backend.mapper;

import com.motorvault.backend.dto.request.VehicleRequest;
import com.motorvault.backend.dto.response.VehicleResponse;
import com.motorvault.backend.entity.Vehicle;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Vehicle toEntity(VehicleRequest request);

    @Mapping(source = "usuario.nombre", target = "usuarioNombre")
    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    void updateEntity(VehicleRequest request, @MappingTarget Vehicle vehicle);
}
