package com.charizard.compiled.hangout_service.domain.model.enums;

/**
 * Estados posibles del ciclo de vida de una invitación.
 * <ul>
 *   <li>{@link #PENDING} — invitación enviada, esperando respuesta</li>
 *   <li>{@link #ACCEPTED} — invitación aceptada, se crea membresía</li>
 *   <li>{@link #REJECTED} — invitación rechazada</li>
 * </ul>
 */
public enum InvitationStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
