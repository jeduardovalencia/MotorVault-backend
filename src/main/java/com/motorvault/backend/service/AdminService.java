package com.motorvault.backend.service;

import com.motorvault.backend.dto.request.UpdateUserRoleRequest;
import com.motorvault.backend.dto.response.AdminStatsResponse;
import com.motorvault.backend.dto.response.AdminUserResponse;
import com.motorvault.backend.dto.response.AdminVehicleResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {

    AdminStatsResponse obtenerEstadisticas();

    List<AdminUserResponse> listarUsuarios(String busqueda, Boolean activo);

    AdminUserResponse obtenerUsuario(Long id);

    AdminUserResponse toggleUsuario(Long id, Long adminId, String adminEmail,
                                    HttpServletRequest request);

    AdminUserResponse cambiarRol(Long id, UpdateUserRoleRequest body,
                                 Long adminId, String adminEmail,
                                 HttpServletRequest request);

    List<AdminVehicleResponse> listarVehiculos(String placa, String marca,
                                               Integer anio, LocalDate desde,
                                               LocalDate hasta);

    void eliminarVehiculo(Long id, Long adminId, String adminEmail,
                          HttpServletRequest request);
}
