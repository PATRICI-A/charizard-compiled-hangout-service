package com.charizard.compiled.hangout_service.application.usecase;

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
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .ownerId(UUID.randomUUID())
                .status(ParcheStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("closeParche lanza ParcheNotFoundException cuando el parche no existe")
    void closeParche_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.closeParche(parcheId))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(parcheId.toString());

        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("closeParche cambia el status a FILED y guarda (solo ROLE_ADMIN llega aquí)")
    void closeParche_adminPuede_cambiaStatusYGuarda() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        useCase.closeParche(parcheId);

        assertThat(parche.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository).save(parche);
    }
}
