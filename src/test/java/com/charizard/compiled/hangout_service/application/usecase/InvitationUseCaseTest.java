package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.InvitationResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.DuplicateInvitationException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheEventPublisherPort;
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
class InvitationUseCaseTest {

    @Mock InvitationRepositoryPort invitationRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheRepositoryPort parcheRepository;
    @Mock ParcheEventPublisherPort parcheEventPublisher;

    @InjectMocks InvitationUseCase useCase;

    private UUID parcheId;
    private UUID inviterId;
    private UUID studentId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        inviterId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Privado")
                .ownerId(UUID.randomUUID())
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PRIVATE)
                .build();
    }

    @Test
    @DisplayName("sendInvitation lanza ParcheNotFoundException cuando el parche no existe")
    void sendInvitation_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, inviterId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation lanza ResponseStatusException 403 cuando el invitador no es miembro del parche")
    void sendInvitation_noEsMiembro_lanzaForbidden() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, inviterId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, inviterId, studentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation lanza StudentAlreadyMemberException cuando el invitado ya es miembro")
    void sendInvitation_estudianteYaMiembro_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, inviterId)).thenReturn(true);
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, inviterId, studentId))
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
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, inviterId)).thenReturn(true);
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, inviterId, studentId))
                .isInstanceOf(DuplicateInvitationException.class);

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("sendInvitation guarda la invitación PENDING y publica InvitationSentEvent cuando todo es válido")
    void sendInvitation_valido_guardaYPublicaEvento() {
        Invitation saved = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .inviterId(inviterId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, inviterId)).thenReturn(true);
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.empty());
        when(invitationRepository.save(any())).thenReturn(saved);

        InvitationResponse result = useCase.sendInvitation(parcheId, inviterId, studentId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(result.getInvitedStudentId()).isEqualTo(studentId);
        verify(invitationRepository).save(any(Invitation.class));
        verify(parcheEventPublisher).publishInvitationSent(any(), any(), any(), any());
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
                .inviterId(inviterId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, inviterId)).thenReturn(true);
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.of(rejected));
        when(invitationRepository.save(any())).thenReturn(saved);

        InvitationResponse result = useCase.sendInvitation(parcheId, inviterId, studentId);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("cualquier miembro (no solo el owner) puede enviar invitación")
    void sendInvitation_cualquierMiembroPuedeInvitar() {
        UUID nonOwnerMemberId = UUID.randomUUID(); // distinto al parche.ownerId
        Invitation saved = Invitation.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .inviterId(nonOwnerMemberId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, nonOwnerMemberId)).thenReturn(true);
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.empty());
        when(invitationRepository.save(any())).thenReturn(saved);

        assertThatCode(() -> useCase.sendInvitation(parcheId, nonOwnerMemberId, studentId))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("sendInvitation no publica evento cuando el parche no existe")
    void sendInvitation_parcheNoExiste_noPublicaEvento() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.sendInvitation(parcheId, inviterId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(parcheEventPublisher, never()).publishInvitationSent(any(), any(), any(), any());
    }
}
