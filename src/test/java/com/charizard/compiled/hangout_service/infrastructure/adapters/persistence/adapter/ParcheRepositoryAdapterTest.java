package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.ParcheEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcheRepositoryAdapterTest {

    @Mock ParcheRepository parcheRepository;
    @Mock ParcheEntityMapper mapper;

    @InjectMocks ParcheRepositoryAdapter adapter;

    private UUID parcheId;
    private Parche parche;
    private ParcheEntity parcheEntity;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        parche = Parche.builder().id(parcheId).name("Test").status(ParcheStatus.ACTIVE).build();
        parcheEntity = new ParcheEntity();
    }

    @Test
    @DisplayName("findById retorna el parche mapeado cuando existe en el repositorio")
    void findById_existe_retornaParcheMapeado() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        Optional<Parche> result = adapter.findById(parcheId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("findById retorna Optional vacío cuando no existe en el repositorio")
    void findById_noExiste_retornaVacio() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        Optional<Parche> result = adapter.findById(parcheId);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any(ParcheEntity.class));
    }

    @Test
    @DisplayName("save convierte a entidad, persiste y retorna el dominio mapeado")
    void save_persisteEntidadYRetornaDominio() {
        when(mapper.toEntity(parche)).thenReturn(parcheEntity);
        when(parcheRepository.save(parcheEntity)).thenReturn(parcheEntity);
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        Parche result = adapter.save(parche);

        assertThat(result).isEqualTo(parche);
        verify(mapper).toEntity(parche);
        verify(parcheRepository).save(parcheEntity);
        verify(mapper).toDomain(parcheEntity);
    }

    @Test
    @DisplayName("findArchivables delega con LocalDate y LocalTime correctos y mapea resultados")
    void findArchivables_delegaConFechaYHoraYMapea() {
        LocalDate thresholdDate = LocalDate.now().minusDays(1);
        LocalTime thresholdTime = LocalTime.of(10, 0);

        when(parcheRepository.findArchivables(ParcheStatus.ACTIVE, thresholdDate, thresholdTime))
                .thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findArchivables(ParcheStatus.ACTIVE, thresholdDate, thresholdTime);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(parche);
        verify(parcheRepository).findArchivables(ParcheStatus.ACTIVE, thresholdDate, thresholdTime);
    }

    @Test
    @DisplayName("findArchivables retorna lista vacía cuando no hay parches archivables")
    void findArchivables_sinResultados_retornaListaVacia() {
        LocalDate thresholdDate = LocalDate.now().minusDays(1);
        LocalTime thresholdTime = LocalTime.now();

        when(parcheRepository.findArchivables(ParcheStatus.ACTIVE, thresholdDate, thresholdTime))
                .thenReturn(List.of());

        List<Parche> result = adapter.findArchivables(ParcheStatus.ACTIVE, thresholdDate, thresholdTime);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByFilters sin filtros invoca findAll con Specification y mapea resultados")
    @SuppressWarnings("unchecked")
    void findByFilters_sinFiltros_llamaFindAllYMapea() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findByFilters(null, null, null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("findByFilters con todos los filtros invoca findAll")
    @SuppressWarnings("unchecked")
    void findByFilters_conTodosLosFiltros_llamaFindAll() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findByFilters(
                ParcheType.PUBLIC, ParcheStatus.ACTIVE, "futbol", LocalDate.of(2026, 6, 15));

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByFilters con nombre en blanco no aplica filtro de nombre")
    @SuppressWarnings("unchecked")
    void findByFilters_nombreEnBlanco_noAplicaFiltroNombre() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<Parche> result = adapter.findByFilters(null, null, "   ", null);

        assertThat(result).isEmpty();
        verify(parcheRepository).findAll(any(Specification.class));
    }
}
