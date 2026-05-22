package com.charizard.compiled.hangout_service.domain.ports.out;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de parches.
 * Define las operaciones que el dominio necesita del repositorio,
 * sin exponer detalles de implementación (JPA, JDBC, etc.).
 */
public interface ParcheRepositoryPort {
    /** Busca un parche por su ID */
    Optional<Parche> findById(UUID id);
    /** Guarda o actualiza un parche en la base de datos */
    Parche save(Parche parche);
    /**
     * Busca parches públicos activos aplicando filtros opcionales.
     * Siempre fuerza PUBLIC + ACTIVE para la búsqueda pública.
     */
    List<Parche> findByFilters(String nombre, LocalDate fecha, String categoria, String lugar);
    /** Busca parches activos cuya fecha de realización ya expiró */
    List<Parche> findArchivables(ParcheStatus status, LocalDate thresholdDate, LocalTime thresholdTime);
    /** Busca parches activos (PUBLIC o PRIVATE) donde el usuario es miembro */
    List<Parche> findActiveByIds(List<UUID> parcheIds);
}
