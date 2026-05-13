package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;

import java.util.UUID;

/**
 * Puerto de salida para la persistencia de membresías.
 * Define las operaciones que el dominio necesita para gestionar
 * la relación entre estudiantes y parches.
 */
public interface MemberRepositoryPort {
    /** Verifica si un estudiante ya es miembro de un parche */
    boolean existsByParcheIdAndStudentId(UUID parcheId, UUID studentId);
    /** Verifica si un estudiante tiene un rol específico en un parche */
    boolean existsByParcheIdAndStudentIdAndMemberRole(UUID parcheId, UUID studentId, MemberRole role);
    /** Cuenta los parches activos de un estudiante (límite: 5) */
    int countParchesActivosByStudentId(UUID studentId);
    /** Cuenta los miembros actuales de un parche */
    int countByParcheId(UUID parcheId);
    /** Guarda o actualiza una membresía */
    Member save(Member member);
    /** Elimina la membresía de un estudiante en un parche */
    void deleteByParcheIdAndStudentId(UUID parcheId, UUID studentId);
}
