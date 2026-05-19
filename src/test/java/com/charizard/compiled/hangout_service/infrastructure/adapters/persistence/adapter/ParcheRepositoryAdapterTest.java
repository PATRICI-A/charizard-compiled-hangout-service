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

import static org.assertj.core.api.Assertions.assertThat;
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
        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .build();
        parcheEntity = new ParcheEntity();
    }

    @Test
    @DisplayName("findById retorna el parche mapeado cuando existe")
    void findById_existe_retornaDominio() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        Optional<Parche> result = adapter.findById(parcheId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(parcheId);
        verify(mapper).toDomain(parcheEntity);
    }

    @Test
    @DisplayName("findById retorna Optional vacío cuando no existe")
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
    @DisplayName("findArchivables delega al repositorio y mapea los resultados")
    void findArchivables_retornaListaMapeada() {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        when(parcheRepository.findArchivables(ParcheStatus.ACTIVE, date, time))
                .thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findArchivables(ParcheStatus.ACTIVE, date, time);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("findByFilters con nombre retorna parches PUBLIC+ACTIVE que coinciden")
    void findByFilters_conNombre_retornaResultados() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findByFilters("Test", null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("findByFilters sin filtros retorna todos los PUBLIC+ACTIVE")
    void findByFilters_sinFiltros_retornaListaCompleta() {
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity, parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findByFilters(null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findActiveByIds con lista vacía retorna lista vacía sin consultar el repositorio")
    void findActiveByIds_listaVacia_retornaVacioSinConsultar() {
        List<Parche> result = adapter.findActiveByIds(List.of());

        assertThat(result).isEmpty();
        verify(parcheRepository, never()).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("findActiveByIds con IDs válidos retorna los parches activos correspondientes")
    void findActiveByIds_conIds_retornaParchesActivos() {
        List<UUID> ids = List.of(parcheId, UUID.randomUUID());
        when(parcheRepository.findAll(any(Specification.class))).thenReturn(List.of(parcheEntity));
        when(mapper.toDomain(parcheEntity)).thenReturn(parche);

        List<Parche> result = adapter.findActiveByIds(ids);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findAll(any(Specification.class));
    }
}
