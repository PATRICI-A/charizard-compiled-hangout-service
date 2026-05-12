package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidad JPA que mapea la tabla {@code parches} en PostgreSQL.
 * Representa la persistencia de un parche con sus datos completos.
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parches")
public class ParcheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(nullable = false)
    private String place;

    @Enumerated(EnumType.STRING)
    private ParcheCategory category;

    @Enumerated(EnumType.STRING)
    private ParcheType type;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime hour;

    @Column(nullable = false)
    @Min(2) @Max(30)
    private int maximumQuota;

    @Enumerated(EnumType.STRING)
    private ParcheStatus status;

    @Column(nullable = false)
    private UUID captainId;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @Column(nullable = true)
    private UUID eventId;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }
}
