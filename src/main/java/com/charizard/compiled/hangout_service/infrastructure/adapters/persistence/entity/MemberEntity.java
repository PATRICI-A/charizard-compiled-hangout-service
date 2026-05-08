package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
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

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @PrePersist
    private void prePersist() {
        this.unionDate = LocalDateTime.now();
    }
}
