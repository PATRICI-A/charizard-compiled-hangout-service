package com.charizard.compiled.hangout_service.domain.model.enums;

/**
 * Estados del ciclo de vida de un parche.
 * <ul>
 *   <li>{@link #ACTIVE} — parche vigente, visible y operable</li>
 *   <li>{@link #FILED} — parche archivado (vencido o cerrado por el capitán)</li>
 * </ul>
 */
public enum ParcheStatus {
    ACTIVE,
    FILED
}
