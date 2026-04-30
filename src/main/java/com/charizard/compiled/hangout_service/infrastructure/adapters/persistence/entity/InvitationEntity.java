package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

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
public class InvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parche_id", nullable = false)
    private UUID parcheId;

    @Column(name = "capitan_id", nullable = false)
    private UUID captainId;

    @Column(name = "estudiante_invitado_id", nullable = false)
    private UUID invitedStudentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "fecha_respuesta")
    private LocalDateTime respondedAt;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
    }
}
