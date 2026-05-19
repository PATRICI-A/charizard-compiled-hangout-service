package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.InvitationAlreadyRespondedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.NotificacionPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RespondInvitationUseCaseTest {

    @Mock InvitationRepositoryPort invitationRepository;
    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheEventPublisherPort parcheEventPublisher;
    @Mock NotificacionPort notificacionPort;

    @InjectMocks RespondInvitationUseCase useCase;

    private UUID invitationId;
    private UUID studentId;
    private UUID parcheId;
    private Invitation pendingInvitation;
    private Parche parche;

    @BeforeEach
    void setUp() {
        invitationId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        parcheId = UUID.randomUUID();

        pendingInvitation = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .inviterId(UUID.randomUUID())
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        parche = Parche.builder()
                .id(parcheId)
                .name("Test Parche")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PRIVATE)
                .ownerId(UUID.randomUUID())
                .build();
    }

    // ─── Validaciones comunes ──────────────────────────────────────────────

    @Test
    @DisplayName("respondInvitation lanza ResponseStatusException cuando el respondedor no es el estudiante invitado")
    void respondInvitation_noEsElEstudianteInvitado_lanzaForbidden() {
        UUID otroEstudiante = UUID.randomUUID();
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, otroEstudiante, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not the invited student");

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("respondInvitation lanza InvitationAlreadyRespondedException cuando la invitación ya fue respondida")
    void respondInvitation_invitacionYaRespondida_lanzaExcepcion() {
        Invitation accepted = Invitation.builder()
                .id(invitationId).parcheId(parcheId)
                .invitedStudentId(studentId).status(InvitationStatus.ACCEPTED)
                .build();
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(accepted));

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.REJECTED))
                .isInstanceOf(InvitationAlreadyRespondedException.class);
    }

    // ─── Flujo ACCEPTED ───────────────────────────────────────────────────

    @Test
    @DisplayName("respondInvitation ACCEPTED lanza ResponseStatusException cuando el parche está lleno")
    void respondInvitation_accepted_parcheCompleto_lanzaExcepcion() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10);

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("full");

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("respondInvitation ACCEPTED lanza MaxHangoutsReachedException cuando el student tiene 5 activos")
    void respondInvitation_accepted_estudianteCon5Activos_lanzaExcepcion() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(MaxHangoutsReachedException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("respondInvitation ACCEPTED crea member STUDENT, notifica y publica InvitationAcceptedEvent")
    void respondInvitation_accepted_valido_creaMemberYPublicaEvento() {
        Invitation savedInvitation = Invitation.builder()
                .id(invitationId).parcheId(parcheId)
                .inviterId(pendingInvitation.getInviterId())
                .invitedStudentId(studentId).status(InvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(4);
        when(memberRepository.save(any())).thenReturn(null);
        when(invitationRepository.save(any())).thenReturn(savedInvitation);

        var result = useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        verify(memberRepository).save(argThat(m ->
                m.getStudentId().equals(studentId)
        ));
        verify(notificacionPort).notificarNuevoMiembro(any(), eq(studentId), anyString());
        verify(parcheEventPublisher).publishInvitationAccepted(eq(invitationId), eq(parcheId), eq(studentId), any());
    }

    @Test
    @DisplayName("respondInvitation ACCEPTED publica InvitationAcceptedEvent exactamente una vez")
    void respondInvitation_accepted_publicaEventoExactamenteUnaVez() {
        Invitation savedInvitation = Invitation.builder()
                .id(invitationId).parcheId(parcheId)
                .inviterId(pendingInvitation.getInviterId())
                .invitedStudentId(studentId).status(InvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(0);
        when(memberRepository.save(any())).thenReturn(null);
        when(invitationRepository.save(any())).thenReturn(savedInvitation);

        useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED);

        verify(parcheEventPublisher, times(1)).publishInvitationAccepted(any(), any(), any(), any());
    }

    @Test
    @DisplayName("respondInvitation ACCEPTED no publica evento cuando el parche está lleno")
    void respondInvitation_accepted_parcheCompleto_noPublicaEvento() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10);

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class);

        verify(parcheEventPublisher, never()).publishInvitationAccepted(any(), any(), any(), any());
    }

    // ─── Flujo REJECTED ───────────────────────────────────────────────────

    @Test
    @DisplayName("respondInvitation REJECTED no crea member y guarda invitación con status REJECTED")
    void respondInvitation_rejected_noCreaMemberYGuardaRejected() {
        Invitation savedInvitation = Invitation.builder()
                .id(invitationId).parcheId(parcheId)
                .inviterId(pendingInvitation.getInviterId())
                .invitedStudentId(studentId).status(InvitationStatus.REJECTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(invitationRepository.save(any())).thenReturn(savedInvitation);

        var result = useCase.respondInvitation(invitationId, studentId, InvitationStatus.REJECTED);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.REJECTED);
        verify(memberRepository, never()).save(any());
        verify(parcheRepository, never()).findById(any());
        verify(parcheEventPublisher, never()).publishInvitationAccepted(any(), any(), any(), any());
    }
}
