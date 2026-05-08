package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.events.InvitationSentEvent;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationUseCaseTest {

    @Mock InvitationRepositoryPort invitationRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheRepositoryPort parcheRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks InvitationUseCase useCase;

    private UUID parcheId;
    private UUID captainId;
    private UUID studentId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        captainId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Privado")
                .captainId(captainId)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("sendInvitation lanza ParcheNotFoundException cuando el parche no existe")
    void sendInvitation_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, captainId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation lanza ResponseStatusException 403 cuando el solicitante no es el capitán")
    void sendInvitation_noEsCaptain_lanzaForbidden() {
        UUID otroUsuario = UUID.randomUUID();
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, otroUsuario, studentId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("captain");

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation lanza StudentAlreadyMemberException cuando el invitado ya es miembro")
    void sendInvitation_estudianteYaMiembro_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, captainId, studentId))
                .isInstanceOf(StudentAlreadyMemberException.class);

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation lanza DuplicateInvitationException cuando ya hay una invitación PENDING")
    void sendInvitation_invitacionPendienteExistente_lanzaExcepcion() {
        Invitation pending = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, captainId, studentId))
                .isInstanceOf(DuplicateInvitationException.class);

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation guarda la invitación PENDING y publica InvitationSentEvent cuando todo es válido")
    void sendInvitation_valido_guardaYPublicaEvento() {
        Invitation saved = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .captainId(captainId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.empty());
        when(invitationRepository.save(any())).thenReturn(saved);

        InvitationResponse result = useCase.sendInvitation(parcheId, captainId, studentId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(result.getInvitedStudentId()).isEqualTo(studentId);
        verify(invitationRepository).save(any(Invitation.class));
        verify(eventPublisher).publishEvent(any(InvitationSentEvent.class));
    }

    @Test
    @DisplayName("sendInvitation permite reenviar invitación cuando la anterior fue REJECTED")
    void sendInvitation_invitacionRechazadaPrevia_permiteReenviar() {
        Invitation rejected = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.REJECTED)
                .build();
        Invitation saved = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .captainId(captainId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.of(rejected));
        when(invitationRepository.save(any())).thenReturn(saved);

        InvitationResponse result = useCase.sendInvitation(parcheId, captainId, studentId);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("sendInvitation no publica evento cuando el parche no existe")
    void sendInvitation_parcheNoExiste_noPublicaEvento() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, captainId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(eventPublisher, never()).publishEvent(any());
    }
}
