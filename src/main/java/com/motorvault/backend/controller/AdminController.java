package com.motorvault.backend.controller;

import com.motorvault.backend.dto.request.UpdateUserRoleRequest;
import com.motorvault.backend.dto.response.*;
import com.motorvault.backend.security.CustomUserDetails;
import com.motorvault.backend.service.ActivityLogService;
import com.motorvault.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Panel de administración")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;
    private final ActivityLogService activityLogService;

    // ── Estadísticas ───────────────────────────────────────────────────────

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas generales del sistema")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> obtenerEstadisticas() {
        return ResponseEntity.ok(ApiResponse.ok(adminService.obtenerEstadisticas()));
    }

    // ── Usuarios ───────────────────────────────────────────────────────────

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuarios con filtros opcionales")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> listarUsuarios(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.listarUsuarios(busqueda, activo)));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<ApiResponse<AdminUserResponse>> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.obtenerUsuario(id)));
    }

    @PatchMapping("/usuarios/{id}/toggle")
    @Operation(summary = "Activar o desactivar un usuario")
    public ResponseEntity<ApiResponse<AdminUserResponse>> toggleUsuario(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                adminService.toggleUsuario(id, getAdminId(), getAdminEmail(), request)));
    }

    @PatchMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar el rol de un usuario")
    public ResponseEntity<ApiResponse<AdminUserResponse>> cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                adminService.cambiarRol(id, body, getAdminId(), getAdminEmail(), request)));
    }

    // ── Vehículos ──────────────────────────────────────────────────────────

    @GetMapping("/vehiculos")
    @Operation(summary = "Listar todos los vehículos con filtros opcionales")
    public ResponseEntity<ApiResponse<List<AdminVehicleResponse>>> listarVehiculos(
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.ok(
                adminService.listarVehiculos(placa, marca, anio, desde, hasta)));
    }

    @DeleteMapping("/vehiculos/{id}")
    @Operation(summary = "Eliminar vehículo (soft delete)")
    public ResponseEntity<ApiResponse<Void>> eliminarVehiculo(
            @PathVariable Long id,
            HttpServletRequest request) {
        adminService.eliminarVehiculo(id, getAdminId(), getAdminEmail(), request);
        return ResponseEntity.ok(ApiResponse.ok("Vehículo eliminado", null));
    }

    // ── Logs ───────────────────────────────────────────────────────────────

    @GetMapping("/logs")
    @Operation(summary = "Consultar logs de actividad con filtros opcionales")
    public ResponseEntity<ApiResponse<List<ActivityLogResponse>>> listarLogs(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<ActivityLogResponse> logs;

        if (usuarioId != null) {
            logs = activityLogService.listarPorUsuario(usuarioId);
        } else if (accion != null && !accion.isBlank()) {
            logs = activityLogService.listarPorAccion(accion);
        } else if (desde != null && hasta != null) {
            logs = activityLogService.listarPorFechas(desde, hasta);
        } else {
            logs = activityLogService.listarTodos();
        }

        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private CustomUserDetails getPrincipal() {
        return (CustomUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
    }

    private Long getAdminId() {
        return getPrincipal().getId();
    }

    private String getAdminEmail() {
        return getPrincipal().getUsername();
    }
}
