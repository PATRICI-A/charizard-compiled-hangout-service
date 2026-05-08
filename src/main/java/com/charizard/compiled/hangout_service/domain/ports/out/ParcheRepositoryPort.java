package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParcheRepositoryPort {
    Optional<Parche> findById(UUID id);
    Parche save(Parche parche);
    List<Parche> findByFilters(ParcheType type, ParcheStatus status, String nombre, LocalDate fecha);
    List<Parche> findArchivables(ParcheStatus status, LocalDate thresholdDate, LocalTime thresholdTime);
}
