package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;

    @InjectMocks CloseParcheUseCase useCase;

    private UUID parcheId;
    private UUID captainId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        captainId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .captainId(captainId)
                .status(ParcheStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("closeParche lanza ParcheNotFoundException cuando no existe")
    void closeParche_noExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.closeParche(parcheId, captainId))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(parcheId.toString());

        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("closeParche lanza AccessDeniedException cuando solicitante no es captain")
    void closeParche_noEsCaptain_lanzaAccessDenied() {
        UUID otro = UUID.randomUUID();
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        assertThatThrownBy(() -> useCase.closeParche(parcheId, otro))
                .isInstanceOf(AccessDeniedException.class);

        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("closeParche cambia status a FILED cuando captain es correcto")
    void closeParche_captainCorrecto_cambiaStatusAFiled() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        useCase.closeParche(parcheId, captainId);

        assertThat(parche.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository).save(parche);
    }
}
