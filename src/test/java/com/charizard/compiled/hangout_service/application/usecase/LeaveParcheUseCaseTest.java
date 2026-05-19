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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;

    @InjectMocks LeaveParcheUseCase useCase;

    private UUID parcheId;
    private UUID studentId;
    private UUID ownerId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .status(ParcheStatus.ACTIVE)
                .ownerId(ownerId)
                .build();
    }

    @Test
    @DisplayName("salirDeParche lanza ParcheNotFoundException cuando el parche no existe")
    void salirDeParche_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId, null))
                .isInstanceOf(ParcheNotFoundException.class);

        verifyNoInteractions(memberRepository);
    }

    @Test
    @DisplayName("salirDeParche lanza ResponseStatusException 404 cuando el student no es miembro")
    void salirDeParche_noEsMiembro_lanza404() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche lanza IllegalArgumentException cuando el parche está archivado")
    void salirDeParche_parcheArchivado_lanzaIllegalArgument() {
        parche.setStatus(ParcheStatus.FILED);
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, studentId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("archived");

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }

    @Test
    @DisplayName("salirDeParche (no-owner) elimina el member sin necesitar newOwnerId")
    void salirDeParche_noOwner_eliminaMemberDirectamente() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        useCase.salirDeParche(parcheId, studentId, null);

        verify(memberRepository).deleteByParcheIdAndStudentId(parcheId, studentId);
        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("salirDeParche (owner único miembro) archiva el parche y elimina el member")
    void salirDeParche_ownerUnicoMiembro_archivaParcheYEliminaMember() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, ownerId)).thenReturn(true);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        useCase.salirDeParche(parcheId, ownerId, null);

        assertThat(parche.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository).save(parche);
        verify(memberRepository).deleteByParcheIdAndStudentId(parcheId, ownerId);
    }

    @Test
    @DisplayName("salirDeParche (owner con más miembros sin newOwnerId) lanza 400")
    void salirDeParche_ownerConMiembrosSinNewOwner_lanza400() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, ownerId)).thenReturn(true);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);

        assertThatThrownBy(() -> useCase.salirDeParche(parcheId, ownerId, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));

        verify(memberRepository, never()).deleteByParcheIdAndStudentId(any(), any());
    }


}
