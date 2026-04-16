package com.motorvault.backend.service.impl;

import com.motorvault.backend.dto.request.UpdateUserRoleRequest;
import com.motorvault.backend.dto.response.AdminStatsResponse;
import com.motorvault.backend.dto.response.AdminUserResponse;
import com.motorvault.backend.dto.response.AdminVehicleResponse;
import com.motorvault.backend.entity.User;
import com.motorvault.backend.entity.Vehicle;
import com.motorvault.backend.entity.enums.Role;
import com.motorvault.backend.exception.BusinessException;
import com.motorvault.backend.exception.ResourceNotFoundException;
import com.motorvault.backend.repository.UserRepository;
import com.motorvault.backend.repository.VehicleRepository;
import com.motorvault.backend.service.ActivityLogService;
import com.motorvault.backend.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse obtenerEstadisticas() {
        long totalUsuarios = userRepository.count();
        long usuariosActivos = userRepository.countByEnabled(true);
        long totalVehiculos = vehicleRepository.countByActivoTrue();

        List<Object[]> rawMarcas = vehicleRepository.contarPorMarca();
        Map<String, Long> vehiculosPorMarca = new LinkedHashMap<>();
        for (Object[] row : rawMarcas) {
            vehiculosPorMarca.put((String) row[0], (Long) row[1]);
        }

        return AdminStatsResponse.builder()
                .totalUsuarios(totalUsuarios)
                .usuariosActivos(usuariosActivos)
                .totalVehiculos(totalVehiculos)
                .vehiculosPorMarca(vehiculosPorMarca)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> listarUsuarios(String busqueda, Boolean activo) {
        List<User> usuarios;

        if (busqueda != null && !busqueda.isBlank()) {
            usuarios = userRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            busqueda, busqueda, busqueda);
        } else if (activo != null) {
            usuarios = userRepository.findByEnabledOrderByCreatedAtDesc(activo);
        } else {
            usuarios = userRepository.findAllByOrderByCreatedAtDesc();
        }

        return usuarios.stream().map(this::toUserResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse obtenerUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse toggleUsuario(Long id, Long adminId, String adminEmail,
                                           HttpServletRequest request) {
        if (id.equals(adminId)) {
            throw new BusinessException("No puedes desactivarte a ti mismo");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (user.isEnabled() && user.getRole() == Role.ROLE_ADMIN) {
            long totalAdmins = userRepository.countByRole(Role.ROLE_ADMIN);
            if (totalAdmins <= 1) {
                throw new BusinessException("No puedes desactivar al último administrador");
            }
        }

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        String accion = user.isEnabled() ? "ADMIN_UNBLOCK_USER" : "ADMIN_BLOCK_USER";
        String descripcion = (user.isEnabled() ? "Usuario desbloqueado: " : "Usuario bloqueado: ")
                + user.getEmail();
        activityLogService.registrar(accion, descripcion, adminId, adminEmail, request);

        return toUserResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse cambiarRol(Long id, UpdateUserRoleRequest body,
                                        Long adminId, String adminEmail,
                                        HttpServletRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Role nuevoRol = Role.valueOf(body.getRole());

        if (user.getRole() == Role.ROLE_ADMIN && nuevoRol != Role.ROLE_ADMIN) {
            long totalAdmins = userRepository.countByRole(Role.ROLE_ADMIN);
            if (totalAdmins <= 1) {
                throw new BusinessException("No puedes quitar el rol ADMIN al último administrador");
            }
        }

        Role rolAnterior = user.getRole();
        user.setRole(nuevoRol);
        userRepository.save(user);

        activityLogService.registrar(
                "ADMIN_CHANGE_ROLE",
                "Rol cambiado de " + rolAnterior.name() + " a " + nuevoRol.name()
                        + " para: " + user.getEmail(),
                adminId, adminEmail, request);

        return toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminVehicleResponse> listarVehiculos(String placa, String marca,
                                                       Integer anio, LocalDate desde,
                                                       LocalDate hasta) {
        LocalDateTime desdeTime = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaTime = hasta != null ? hasta.atTime(23, 59, 59) : null;

        return vehicleRepository
                .buscarAdminConFiltros(placa, marca, anio, desdeTime, hastaTime)
                .stream()
                .map(this::toVehicleResponse)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarVehiculo(Long id, Long adminId, String adminEmail,
                                 HttpServletRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));

        vehicle.setActivo(false);
        vehicleRepository.save(vehicle);

        activityLogService.registrar(
                "ADMIN_DELETE_VEHICLE",
                "Vehículo eliminado: " + vehicle.getPlaca()
                        + " (dueño: " + vehicle.getUsuario().getEmail() + ")",
                adminId, adminEmail, request);
    }

    // ── Mappers privados ───────────────────────────────────────────────────

    private AdminUserResponse toUserResponse(User user) {
        int totalVehiculos = (int) vehicleRepository.countByUsuarioIdAndActivoTrue(user.getId());
        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .totalVehiculos(totalVehiculos)
                .creadoEn(user.getCreatedAt())
                .build();
    }

    private AdminVehicleResponse toVehicleResponse(Vehicle vehicle) {
        User owner = vehicle.getUsuario();
        return AdminVehicleResponse.builder()
                .id(vehicle.getId())
                .placa(vehicle.getPlaca())
                .marca(vehicle.getMarca())
                .modelo(vehicle.getModelo())
                .anio(vehicle.getAnio())
                .color(vehicle.getColor())
                .fotoUrl(vehicle.getFotoUrl())
                .usuarioNombre(owner.getNombre())
                .usuarioEmail(owner.getEmail())
                .usuarioId(owner.getId())
                .creadoEn(vehicle.getCreadoEn())
                .build();
    }
}
