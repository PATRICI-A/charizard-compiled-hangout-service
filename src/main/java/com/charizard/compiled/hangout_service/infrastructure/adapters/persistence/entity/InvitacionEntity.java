package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invitaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parche_id", "estudiante_invitado_id"})
})
public class InvitacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parche_id", nullable = false)
    private UUID parcheId;

    @Column(name = "capitan_id", nullable = false)
    private UUID capitanId;

    @Column(name = "estudiante_invitado_id", nullable = false)
    private UUID estudianteInvitadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInvitacion estado = EstadoInvitacion.PENDIENTE;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
    }
}
