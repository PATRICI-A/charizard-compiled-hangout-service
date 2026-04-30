package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.InvitationAlreadyRespondedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitationRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.NotificacionPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class RespondInvitationUseCaseTest {

    @Mock
    private InvitationRepositoryPort invitationRepository;

    @Mock
    private ParcheRepositoryPort parcheRepository;

    @Mock
    private MemberRepositoryPort memberRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private NotificacionPort notificacionPort;

    @InjectMocks
    private RespondInvitationUseCase useCase;

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
                .captainId(UUID.randomUUID())
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();

        parche = Parche.builder()
                .id(parcheId)
                .name("Test Parche")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PRIVATE)
                .build();
    }

    @Test
    void accept_shouldThrowWhenStudentHas5ActiveHangouts() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(MaxHangoutsReachedException.class)
                .hasMessageContaining("maximum active patches limit (5)");

        verify(memberRepository, never()).save(any());
    }

    @Test
    void accept_shouldAllowWhenStudentHas4ActiveHangouts() {
        Invitation savedInvitation = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .captainId(pendingInvitation.getCaptainId())
                .invitedStudentId(studentId)
                .status(InvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(4);
        when(memberRepository.save(any())).thenReturn(null);
        when(invitationRepository.save(any())).thenReturn(savedInvitation);

        var response = useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED);

        verify(memberRepository, times(1)).save(any());
        assert response.getStatus() == InvitationStatus.ACCEPTED;
    }

    @Test
    void accept_shouldThrowWhenHangoutIsFull() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10);

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("full");

        verify(memberRepository, never()).save(any());
    }

    @Test
    void respond_shouldThrowWhenInvitationAlreadyResponded() {
        Invitation alreadyAccepted = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.ACCEPTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(alreadyAccepted));

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, studentId, InvitationStatus.REJECTED))
                .isInstanceOf(InvitationAlreadyRespondedException.class);
    }

    @Test
    void respond_shouldThrowWhenNotTheInvitedStudent() {
        UUID otherStudent = UUID.randomUUID();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));

        assertThatThrownBy(() -> useCase.respondInvitation(invitationId, otherStudent, InvitationStatus.ACCEPTED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not the invited student");
    }

    @Test
    void reject_shouldNotCreateMembershipAndSaveRejectedStatus() {
        Invitation savedInvitation = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .captainId(pendingInvitation.getCaptainId())
                .invitedStudentId(studentId)
                .status(InvitationStatus.REJECTED)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(pendingInvitation));
        when(invitationRepository.save(any())).thenReturn(savedInvitation);

        var response = useCase.respondInvitation(invitationId, studentId, InvitationStatus.REJECTED);

        verify(memberRepository, never()).save(any());
        verify(parcheRepository, never()).findById(any());
        assert response.getStatus() == InvitationStatus.REJECTED;
    }
}
