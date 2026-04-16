package com.motorvault.backend.service;

import com.motorvault.backend.dto.request.VehicleFilterRequest;
import com.motorvault.backend.dto.request.VehicleRequest;
import com.motorvault.backend.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse registrar(VehicleRequest request, Long usuarioId);

    VehicleResponse actualizar(Long id, VehicleRequest request, Long usuarioId);

    void eliminar(Long id, Long usuarioId);

    List<VehicleResponse> misVehiculos(Long usuarioId);

    List<VehicleResponse> buscar(VehicleFilterRequest filtros, Long usuarioId);

    VehicleResponse obtenerPorId(Long id, Long usuarioId);
}
