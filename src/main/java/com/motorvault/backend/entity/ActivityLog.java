package com.motorvault.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(length = 150)
    private String usuarioEmail;

    @Column(length = 50)
    private String ip;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn;
}
