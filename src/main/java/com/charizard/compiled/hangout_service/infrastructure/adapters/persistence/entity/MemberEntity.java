package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code members} en PostgreSQL.
 * Representa la membresía de un estudiante en un parche,
 * con restricción única de (parche_id, student_id).
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"parche_id", "student_id"})
})
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "parche_id", nullable = false)
    private UUID parcheId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private LocalDateTime unionDate;

    @PrePersist
    private void prePersist() {
        this.unionDate = LocalDateTime.now();
    }
}
