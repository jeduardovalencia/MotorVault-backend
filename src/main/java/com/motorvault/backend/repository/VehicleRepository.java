package com.motorvault.backend.repository;

import com.motorvault.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // ── Métodos existentes ─────────────────────────────────────────────────

    List<Vehicle> findByUsuarioId(Long usuarioId);

    boolean existsByPlacaIgnoreCaseAndUsuarioId(String placa, Long usuarioId);

    boolean existsByPlacaIgnoreCaseAndUsuarioIdAndIdNot(String placa, Long usuarioId, Long id);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.usuario.id = :usuarioId
            AND (:placa IS NULL
              OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :placa, '%')))
            AND (:modelo IS NULL
              OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :modelo, '%')))
            AND (:marca IS NULL
              OR LOWER(v.marca) LIKE LOWER(CONCAT('%', :marca, '%')))
            AND (:anio IS NULL OR v.anio = :anio)
            """)
    List<Vehicle> buscarConFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("placa") String placa,
            @Param("modelo") String modelo,
            @Param("marca") String marca,
            @Param("anio") Integer anio
    );

    // ── Métodos admin ──────────────────────────────────────────────────────

    long countByActivoTrue();

    long countByUsuarioIdAndActivoTrue(Long usuarioId);

    List<Vehicle> findAllByActivoTrueOrderByCreadoEnDesc();

    List<Vehicle> findByPlacaContainingIgnoreCaseAndActivoTrue(String placa);

    List<Vehicle> findByMarcaIgnoreCaseAndActivoTrue(String marca);

    List<Vehicle> findByAnioAndActivoTrue(Integer anio);

    List<Vehicle> findByCreadoEnBetweenAndActivoTrue(LocalDateTime desde, LocalDateTime hasta);

    @Query("""
            SELECT v.marca, COUNT(v)
            FROM Vehicle v
            WHERE v.activo = true
            GROUP BY v.marca
            ORDER BY COUNT(v) DESC
            """)
    List<Object[]> contarPorMarca();

    @Query("""
            SELECT v FROM Vehicle v
            JOIN FETCH v.usuario
            WHERE v.activo = true
            AND (:placa IS NULL OR LOWER(v.placa) LIKE LOWER(CONCAT('%', :placa, '%')))
            AND (:marca IS NULL OR LOWER(v.marca) = LOWER(:marca))
            AND (:anio IS NULL OR v.anio = :anio)
            AND (:desde IS NULL OR v.creadoEn >= :desde)
            AND (:hasta IS NULL OR v.creadoEn <= :hasta)
            ORDER BY v.creadoEn DESC
            """)
    List<Vehicle> buscarAdminConFiltros(
            @Param("placa") String placa,
            @Param("marca") String marca,
            @Param("anio") Integer anio,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
