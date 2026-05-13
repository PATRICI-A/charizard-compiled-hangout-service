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
import org.springframework.http.HttpStatus;
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
    @DisplayName("salirDeParche lanza ParcheNotFoundException cuando el parche no existe")
    void salirDeParche_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche lanza ResponseStatusException 404 cuando el student no es miembro")
    void salirDeParche_noEsMiembro_lanza404() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND))
                .hasMessageContaining("not a member");

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche lanza IllegalArgumentException cuando el parche está archivado")
    void salirDeParche_parcheArchivado_lanzaIllegalArgument() {
        parche.setStatus(ParcheStatus.FILED);
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("archived");

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche lanza IllegalArgumentException cuando el student es el capitán")
    void salirDeParche_estudianteEsCaptain_lanzaIllegalArgument() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, captainId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, captainId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Captain cannot leave");

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche elimina el member cuando todas las validaciones pasan")
    void salirDeParche_valido_eliminaMember() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        useCase.salirDeParche(parcheId, studentId);

        verify(memberRepository).deleteByParcheIdAndStudentId(parcheId, studentId);
    }

    @Test
    @DisplayName("salirDeParche no elimina el member cuando el parche no existe")
    void salirDeParche_parcheNoExiste_noEliminaMember() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verifyNoInteractions(memberRepository);
    }
}
