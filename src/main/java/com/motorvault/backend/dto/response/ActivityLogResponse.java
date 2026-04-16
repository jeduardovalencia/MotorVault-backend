package com.motorvault.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActivityLogResponse {

    private Long id;
    private String accion;
    private String descripcion;
    private String usuarioEmail;
    private String ip;
    private LocalDateTime creadoEn;
}
