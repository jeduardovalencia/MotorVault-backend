package com.motorvault.backend.repository;

import com.motorvault.backend.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findAllByOrderByCreadoEnDesc();

    List<ActivityLog> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    List<ActivityLog> findByAccionOrderByCreadoEnDesc(String accion);

    List<ActivityLog> findByCreadoEnBetweenOrderByCreadoEnDesc(
            LocalDateTime desde, LocalDateTime hasta);

    List<ActivityLog> findTop20ByOrderByCreadoEnDesc();
}
