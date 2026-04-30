package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.events.NuevoMiembroEvent;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import com.charizard.compiled.hangout_service.infrastructure.adapters.notification.NotificacionAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionEventListenerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private InvitationRepositoryPort invitationRepository;

    @Mock
    private ParcheRepositoryPort parcheRepository;

    @Mock
    private MemberRepositoryPort memberRepository;

    private RespondInvitationUseCase useCase;

    private UUID invitationId;
    private UUID studentId;
    private UUID parcheId;
    private Invitation pendingInvitation;
    private Parche parche;

    @BeforeEach
    void setUp() {
        NotificacionAdapter notificacionAdapter = new NotificacionAdapter(eventPublisher);
        useCase = new RespondInvitationUseCase(
                invitationRepository, parcheRepository, memberRepository,
                eventPublisher, notificacionAdapter);

        invitationId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        parcheId = UUID.randomUUID();

        pendingInvitation = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .captainId(UUID.randomUUID())
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Futbol")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PRIVATE)
                .build();
    }

    @Test
    void accept_shouldPublishNuevoMiembroEventExactlyOnce() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(0);
        when(memberRepository.save(any())).thenReturn(null);
        when(invitationRepository.save(any())).thenReturn(
                Invitation.builder().id(invitationId).parcheId(parcheId)
                        .invitedStudentId(studentId).status(InvitationStatus.ACCEPTED).build());

        useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED);

        verify(eventPublisher, times(1)).publishEvent(any(NuevoMiembroEvent.class));
    }

    @Test
    void accept_shouldNotPublishNuevoMiembroEventWhenHangoutIsFull() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10); // igual al cupo máximo

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("full");

        verify(eventPublisher, never()).publishEvent(any(NuevoMiembroEvent.class));
    }
}
