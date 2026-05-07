package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;

    @InjectMocks LeaveParcheUseCase useCase;

    private UUID parcheId;
    private UUID studentId;
    private UUID captainId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        captainId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
    }

    @Test
    @DisplayName("salirDeParche lanza ParcheNotFoundException cuando no existe")
    void salirDeParche_noExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche lanza ResponseStatusException cuando student no es miembro")
    void salirDeParche_noEsMiembro_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    @DisplayName("salirDeParche lanza IllegalArgumentException cuando parche está archivado")
    void salirDeParche_archivado_lanzaIllegalArgument() {
        parche.setStatus(ParcheStatus.FILED);
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("archived");
    }

    @Test
    @DisplayName("salirDeParche lanza IllegalArgumentException cuando student es captain")
    void salirDeParche_esCaptain_lanzaIllegalArgument() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, captainId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, captainId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Captain cannot leave");
    }

    @Test
    @DisplayName("salirDeParche elimina member cuando todo es válido")
    void salirDeParche_valido_eliminaMember() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        useCase.salirDeParche(parcheId, studentId);

        verify(memberRepository).deleteByParcheIdAndStudentId(parcheId, studentId);
    }
}
