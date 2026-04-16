package com.motorvault.backend.controller;

import com.motorvault.backend.dto.request.VehicleFilterRequest;
import com.motorvault.backend.dto.request.VehicleRequest;
import com.motorvault.backend.dto.response.ApiResponse;
import com.motorvault.backend.dto.response.VehicleResponse;
import com.motorvault.backend.security.CustomUserDetails;
import com.motorvault.backend.service.ActivityLogService;
import com.motorvault.backend.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Gestión de vehículos personales")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ActivityLogService activityLogService;

    @PostMapping
    @Operation(summary = "Registrar vehículo")
    public ResponseEntity<ApiResponse<VehicleResponse>> registrar(
            @Valid @RequestBody VehicleRequest request,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId();
        VehicleResponse response = vehicleService.registrar(request, usuarioId);

        activityLogService.registrar(
                "CREATE_VEHICLE",
                "Placa: " + request.getPlaca(),
                usuarioId,
                getPrincipal().getUsername(),
                httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Vehículo registrado", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vehículo")
    public ResponseEntity<ApiResponse<VehicleResponse>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.actualizar(id, request, getUsuarioId());
        return ResponseEntity.ok(ApiResponse.ok("Vehículo actualizado", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vehículo (soft delete)")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        Long usuarioId = getUsuarioId();
        String email = getPrincipal().getUsername();

        // Obtenemos la placa antes de eliminar para el log
        VehicleResponse vehicle = vehicleService.obtenerPorId(id, usuarioId);
        vehicleService.eliminar(id, usuarioId);

        activityLogService.registrar(
                "DELETE_VEHICLE",
                "Placa: " + vehicle.getPlaca(),
                usuarioId,
                email,
                httpRequest);

        return ResponseEntity.ok(ApiResponse.ok("Vehículo eliminado", null));
    }

    @GetMapping("/mis-vehiculos")
    @Operation(summary = "Listar mis vehículos")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> misVehiculos() {
        List<VehicleResponse> lista = vehicleService.misVehiculos(getUsuarioId());
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar vehículos con filtros")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> buscar(
            @ModelAttribute VehicleFilterRequest filtros) {
        List<VehicleResponse> lista = vehicleService.buscar(filtros, getUsuarioId());
        return ResponseEntity.ok(ApiResponse.ok(lista));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID")
    public ResponseEntity<ApiResponse<VehicleResponse>> obtenerPorId(@PathVariable Long id) {
        VehicleResponse response = vehicleService.obtenerPorId(id, getUsuarioId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private Long getUsuarioId() {
        return getPrincipal().getId();
    }

    private CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
