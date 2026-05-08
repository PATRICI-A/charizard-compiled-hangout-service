package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Invitation;
import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.InvitationEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.InvitationEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationRepositoryAdapterTest {

    @Mock InvitationRepository invitationRepository;
    @Mock InvitationEntityMapper mapper;

    @InjectMocks InvitationRepositoryAdapter adapter;

    private UUID invitationId;
    private UUID parcheId;
    private UUID studentId;
    private Invitation invitation;
    private InvitationEntity invitationEntity;

    @BeforeEach
    void setUp() {
        invitationId = UUID.randomUUID();
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        invitation = Invitation.builder()
                .id(invitationId)
                .parcheId(parcheId)
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();
        invitationEntity = new InvitationEntity();
    }

    @Test
    @DisplayName("save convierte a entidad, persiste y retorna el dominio mapeado")
    void save_persisteEntidadYRetornaDominio() {
        when(mapper.toEntity(invitation)).thenReturn(invitationEntity);
        when(invitationRepository.save(invitationEntity)).thenReturn(invitationEntity);
        when(mapper.toDomain(invitationEntity)).thenReturn(invitation);

        Invitation result = adapter.save(invitation);

        assertThat(result).isEqualTo(invitation);
        verify(mapper).toEntity(invitation);
        verify(invitationRepository).save(invitationEntity);
        verify(mapper).toDomain(invitationEntity);
    }

    @Test
    @DisplayName("findById retorna la invitación mapeada cuando existe")
    void findById_existe_retornaInvitacionMapeada() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.of(invitationEntity));
        when(mapper.toDomain(invitationEntity)).thenReturn(invitation);

        Optional<Invitation> result = adapter.findById(invitationId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(invitationId);
    }

    @Test
    @DisplayName("findById retorna Optional vacío cuando no existe")
    void findById_noExiste_retornaVacio() {
        when(invitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        Optional<Invitation> result = adapter.findById(invitationId);

        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any(InvitationEntity.class));
    }

    @Test
    @DisplayName("findByParcheIdAndInvitedStudentId retorna invitación cuando existe")
    void findByParcheIdAndInvitedStudentId_existe_retornaInvitacion() {
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.of(invitationEntity));
        when(mapper.toDomain(invitationEntity)).thenReturn(invitation);

        Optional<Invitation> result = adapter.findByParcheIdAndInvitedStudentId(parcheId, studentId);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("findByParcheIdAndInvitedStudentId retorna vacío cuando no existe")
    void findByParcheIdAndInvitedStudentId_noExiste_retornaVacio() {
        when(invitationRepository.findByParcheIdAndInvitedStudentId(parcheId, studentId))
                .thenReturn(Optional.empty());

        Optional<Invitation> result = adapter.findByParcheIdAndInvitedStudentId(parcheId, studentId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByInvitedStudentIdAndStatus retorna la lista de invitaciones filtrada")
    void findByInvitedStudentIdAndStatus_retornaLista() {
        when(invitationRepository.findByInvitedStudentIdAndStatus(studentId, InvitationStatus.PENDING))
                .thenReturn(List.of(invitationEntity));
        when(mapper.toDomain(invitationEntity)).thenReturn(invitation);

        List<Invitation> result = adapter.findByInvitedStudentIdAndStatus(studentId, InvitationStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("findByParcheId retorna todas las invitaciones del parche")
    void findByParcheId_retornaTodasLasInvitaciones() {
        when(invitationRepository.findByParcheId(parcheId)).thenReturn(List.of(invitationEntity));
        when(mapper.toDomain(invitationEntity)).thenReturn(invitation);

        List<Invitation> result = adapter.findByParcheId(parcheId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getParcheId()).isEqualTo(parcheId);
    }
}
