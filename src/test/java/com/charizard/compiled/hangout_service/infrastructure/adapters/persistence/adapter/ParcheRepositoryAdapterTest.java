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
import java.time.LocalDateTime;
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
    @DisplayName("findById retorna parche cuando existe")
    void findById_existe_retornaParche() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        Optional<Parche> result = adapter.findById(parcheId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("findById retorna vacío cuando no existe")
    void findById_noExiste_retornaVacio() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        Optional<Parche> result = adapter.findById(parcheId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save convierte a entidad, persiste y retorna dominio")
    void save_persisteYRetornaDominio() {
        when(mapper.toEntity(parche)).thenReturn(parcheEntity);
        when(parcheRepository.save(parcheEntity)).thenReturn(parcheEntity);
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        Parche result = adapter.save(parche);

        assertThat(result).isEqualTo(parche);
        verify(parcheRepository).save(parcheEntity);
    }

    @Test
    @DisplayName("findArchivables delega en repositorio y mapea resultados")
    void findArchivables_delegaYMapea() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        when(parcheRepository.findArchivables(ParcheStatus.ACTIVE, threshold)).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findArchivables(ParcheStatus.ACTIVE, threshold);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(parche);
    }

    @Test
    @DisplayName("findByFilters sin filtros llama findAll con spec")
    @SuppressWarnings("unchecked")
    void findByFilters_sinFiltros_llamaFindAll() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findByFilters(null, null, null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("findByFilters con todos los filtros llama findAll")
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

        List<Parche> result = adapter.findByFilters(null, null, "  ", null);

        assertThat(result).isEmpty();
        verify(parcheRepository).findAll(any(Specification.class));
    }
}
