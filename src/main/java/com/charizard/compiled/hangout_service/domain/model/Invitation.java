package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa una invitación a un parche privado.
 * Tiene un ciclo de vida {@link InvitationStatus#PENDING} → {@link InvitationStatus#ACCEPTED}
 * o {@link InvitationStatus#REJECTED}. Al ser aceptada, se crea automáticamente una membresía.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    /** Identificador único de la invitación */
    private UUID id;
    /** ID del parche al que se invita */
    private UUID parcheId;
    /** ID del capitán que envió la invitación */
    private UUID captainId;
    /** ID del estudiante invitado */
    private UUID invitedStudentId;
    /** Estado actual de la invitación (PENDING, ACCEPTED, REJECTED) */
    private InvitationStatus status;
    /** Fecha y hora en que se envió la invitación */
    private LocalDateTime sentAt;
    /** Fecha y hora en que se respondió la invitación (null si pendiente) */
    private LocalDateTime respondedAt;
}
