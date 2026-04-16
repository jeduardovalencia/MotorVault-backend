package com.motorvault.backend.service;

import com.motorvault.backend.dto.response.ActivityLogResponse;
import com.motorvault.backend.entity.ActivityLog;
import com.motorvault.backend.repository.ActivityLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    @Transactional
    public void registrar(String accion, String descripcion,
                          Long usuarioId, String usuarioEmail,
                          HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }

        activityLogRepository.save(ActivityLog.builder()
                .accion(accion)
                .descripcion(descripcion)
                .usuarioId(usuarioId)
                .usuarioEmail(usuarioEmail)
                .ip(ip)
                .build());
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> listarTodos() {
        return activityLogRepository.findAllByOrderByCreadoEnDesc()
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> listarPorUsuario(Long usuarioId) {
        return activityLogRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> listarPorAccion(String accion) {
        return activityLogRepository.findByAccionOrderByCreadoEnDesc(accion)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> listarPorFechas(LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeTime = desde.atStartOfDay();
        LocalDateTime hastaTime = hasta.atTime(23, 59, 59);
        return activityLogRepository.findByCreadoEnBetweenOrderByCreadoEnDesc(desdeTime, hastaTime)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> ultimosLogs() {
        return activityLogRepository.findTop20ByOrderByCreadoEnDesc()
                .stream().map(this::toResponse).toList();
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .accion(log.getAccion())
                .descripcion(log.getDescripcion())
                .usuarioEmail(log.getUsuarioEmail())
                .ip(log.getIp())
                .creadoEn(log.getCreadoEn())
                .build();
    }
}
