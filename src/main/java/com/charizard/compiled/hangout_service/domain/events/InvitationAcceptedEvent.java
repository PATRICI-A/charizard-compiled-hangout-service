package com.charizard.compiled.hangout_service.domain.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
/**
 * Evento de dominio publicado cuando un estudiante acepta una invitación
 * a un parche privado, lo que dispara la creación automática de la membresía.
 */
public class InvitationAcceptedEvent {

    /** ID de la invitación aceptada */
    private final UUID invitationId;
    /** ID del parche al que se unió */
    private final UUID parcheId;
    /** ID del estudiante que aceptó */
    private final UUID studentId;
    /** ID del capitán del parche */
    private final UUID captainId;
}
