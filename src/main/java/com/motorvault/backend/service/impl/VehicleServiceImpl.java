package com.motorvault.backend.service.impl;

import com.motorvault.backend.dto.request.VehicleFilterRequest;
import com.motorvault.backend.dto.request.VehicleRequest;
import com.motorvault.backend.dto.response.VehicleResponse;
import com.motorvault.backend.entity.Vehicle;
import com.motorvault.backend.exception.BusinessException;
import com.motorvault.backend.exception.ResourceNotFoundException;
import com.motorvault.backend.exception.UnauthorizedException;
import com.motorvault.backend.mapper.VehicleMapper;
import com.motorvault.backend.repository.UserRepository;
import com.motorvault.backend.repository.VehicleRepository;
import com.motorvault.backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public VehicleResponse registrar(VehicleRequest request, Long usuarioId) {
        if (vehicleRepository.existsByPlacaIgnoreCaseAndUsuarioId(request.getPlaca(), usuarioId)) {
            throw new BusinessException("La placa ya está registrada");
        }

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setUsuario(userRepository.getReferenceById(usuarioId));

        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public VehicleResponse actualizar(Long id, VehicleRequest request, Long usuarioId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        if (!vehicle.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No tienes permiso sobre este vehículo");
        }

        if (!vehicle.getPlaca().equalsIgnoreCase(request.getPlaca()) &&
                vehicleRepository.existsByPlacaIgnoreCaseAndUsuarioIdAndIdNot(
                        request.getPlaca(), usuarioId, id)) {
            throw new BusinessException("La placa ya está registrada");
        }

        vehicleMapper.updateEntity(request, vehicle);

        return vehicleMapper.toResponse(vehicleRepository.save(vehicle));
    }

    @Override
    @Transactional
    public void eliminar(Long id, Long usuarioId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        if (!vehicle.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No tienes permiso sobre este vehículo");
        }

        vehicle.setActivo(false);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> misVehiculos(Long usuarioId) {
        return vehicleRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> buscar(VehicleFilterRequest filtros, Long usuarioId) {
        return vehicleRepository.buscarConFiltros(
                        usuarioId,
                        filtros.getPlaca(),
                        filtros.getModelo(),
                        filtros.getMarca(),
                        filtros.getAnio())
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse obtenerPorId(Long id, Long usuarioId) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        if (!vehicle.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No tienes permiso sobre este vehículo");
        }

        return vehicleMapper.toResponse(vehicle);
    }
}
