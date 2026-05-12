package com.charizard.compiled.hangout_service.domain.model.enums;

/**
 * Roles que puede tener un miembro dentro de un parche.
 * <ul>
 *   <li>{@link #CAPTAIN} — creador del parche, puede administrarlo</li>
 *   <li>{@link #STUDENT} — participante invitado o que se unió voluntariamente</li>
 * </ul>
 */
public enum MemberRole {
    STUDENT,
    CAPTAIN
}
