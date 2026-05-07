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

import java.time.LocalDateTime;
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
                .name("Parche 1")
                .status(ParcheStatus.ACTIVE)
                .dateRealization(LocalDateTime.now().minusHours(25))
                .build();

        parche2 = Parche.builder()
                .id(UUID.randomUUID())
                .name("Parche 2")
                .status(ParcheStatus.ACTIVE)
                .dateRealization(LocalDateTime.now().minusHours(30))
                .build();
    }

    @Test
    @DisplayName("archiveExpired archiva todos los parches expirados y retorna count")
    void archiveExpired_conParchesExpirados_archivaTodos() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(List.of(parche1, parche2));

        int count = useCase.archiveExpired();

        assertThat(count).isEqualTo(2);
        assertThat(parche1.getStatus()).isEqualTo(ParcheStatus.FILED);
        assertThat(parche2.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository, times(2)).save(any(Parche.class));
    }

    @Test
    @DisplayName("archiveExpired retorna 0 cuando no hay parches expirados")
    void archiveExpired_sinParchesExpirados_retornaCero() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        int count = useCase.archiveExpired();

        assertThat(count).isZero();
        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("archiveExpired archiva exactamente 1 parche")
    void archiveExpired_conUnParche_archiva1() {
        when(parcheRepository.findArchivables(eq(ParcheStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(List.of(parche1));

        int count = useCase.archiveExpired();

        assertThat(count).isEqualTo(1);
        assertThat(parche1.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository, times(1)).save(parche1);
    }
}
