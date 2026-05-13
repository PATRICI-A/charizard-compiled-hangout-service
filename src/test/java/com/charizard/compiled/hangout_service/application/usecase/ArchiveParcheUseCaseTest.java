package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;

    @InjectMocks ArchiveParcheUseCase useCase;

    private Parche parche1;
    private Parche parche2;

    @BeforeEach
    void setUp() {
        parche1 = Parche.builder()
                .id(UUID.randomUUID())
                .name("Parche Vencido 1")
                .status(ParcheStatus.ACTIVE)
                .date(LocalDate.now().minusDays(2))
                .hour(LocalTime.of(10, 0))
                .build();

        parche2 = Parche.builder()
                .id(UUID.randomUUID())
                .name("Parche Vencido 2")
                .status(ParcheStatus.ACTIVE)
                .date(LocalDate.now().minusDays(3))
                .hour(LocalTime.of(8, 0))
                .build();
    }

    @Test
    @DisplayName("archiveExpired archiva todos los parches expirados y retorna el conteo correcto")
    void archiveExpired_conDosParches_archivaTodosYRetornaConteo() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(List.of(parche1, parche2));

        int count = useCase.archiveExpired();

        assertThat(count).isEqualTo(2);
        assertThat(parche1.getStatus()).isEqualTo(ParcheStatus.FILED);
        assertThat(parche2.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository, times(2)).save(any(Parche.class));
    }

    @Test
    @DisplayName("archiveExpired retorna 0 y no guarda nada cuando no hay parches expirados")
    void archiveExpired_sinParches_retornaCeroSinGuardar() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Collections.emptyList());

        int count = useCase.archiveExpired();

        assertThat(count).isZero();
        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("archiveExpired archiva exactamente 1 parche cuando solo hay uno expirado")
    void archiveExpired_conUnParche_archiva1() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(List.of(parche1));

        int count = useCase.archiveExpired();

        assertThat(count).isEqualTo(1);
        assertThat(parche1.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository, times(1)).save(parche1);
    }

    @Test
    @DisplayName("archiveExpired consulta con status ACTIVE y threshold calculado como now()-24h")
    void archiveExpired_consultaConStatusActiveYThresholdCorrecto() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Collections.emptyList());

        useCase.archiveExpired();

        verify(parcheRepository).findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class));
    }

    @Test
    @DisplayName("archiveExpired cambia el status de ACTIVE a FILED en cada parche")
    void archiveExpired_cambiaStatusAFiled() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(List.of(parche1, parche2));

        useCase.archiveExpired();

        verify(parcheRepository).save(argThat(p -> p.getStatus() == ParcheStatus.FILED && p.getId().equals(parche1.getId())));
        verify(parcheRepository).save(argThat(p -> p.getStatus() == ParcheStatus.FILED && p.getId().equals(parche2.getId())));
    }
}
