package com.charizard.compiled.hangout_service.infrastructure.adapters.scheduler;

import com.charizard.compiled.hangout_service.domain.ports.in.ArchiveParcheInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcheArchiveSchedulerTest {

    @Mock ArchiveParcheInputPort archiveParcheUseCase;

    @InjectMocks ParcheArchiveScheduler scheduler;

    @Test
    @DisplayName("archiveExpiredParches delega en el use case y registra el resultado")
    void archiveExpiredParches_delegaEnUseCaseYRegistra() {
        when(archiveParcheUseCase.archiveExpired()).thenReturn(3);

        scheduler.archiveExpiredParches();

        verify(archiveParcheUseCase).archiveExpired();
    }

    @Test
    @DisplayName("archiveExpiredParches maneja correctamente el caso con 0 parches expirados")
    void archiveExpiredParches_sinExpirados_delegaEnUseCase() {
        when(archiveParcheUseCase.archiveExpired()).thenReturn(0);

        scheduler.archiveExpiredParches();

        verify(archiveParcheUseCase, times(1)).archiveExpired();
    }

    @Test
    @DisplayName("archiveExpiredParches llama al use case exactamente una vez por ejecución")
    void archiveExpiredParches_llamaUseCaseExactamenteUnaVez() {
        when(archiveParcheUseCase.archiveExpired()).thenReturn(5);

        scheduler.archiveExpiredParches();

        verify(archiveParcheUseCase, times(1)).archiveExpired();
        verifyNoMoreInteractions(archiveParcheUseCase);
    }
}
