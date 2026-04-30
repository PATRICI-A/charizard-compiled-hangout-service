package com.charizard.compiled.hangout_service.application.service;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcheServiceTest {

    @Mock
    private ParcheRepository parcheRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ParcheService parcheService;

    private UUID captainId;
    private UUID parcheId;
    private ParcheEntity parcheEntity;

    @BeforeEach
    void setUp() {
        captainId = UUID.randomUUID();
        parcheId = UUID.randomUUID();

        parcheEntity = ParcheEntity.builder()
                .id(parcheId)
                .name("Parche de futbol")
                .description("A jugar")
                .place("Cancha ECCI")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .captainId(captainId)
                .dateRealization(LocalDateTime.of(2026, 6, 15, 15, 30))
                .build();
    }

    @Test
    void createParche_shouldCreateParcheAndAddCaptainAsMember() {
        CreateParcheRequest req = CreateParcheRequest.builder()
                .name("Parche de futbol")
                .description("A jugar")
                .place("Cancha ECCI")
                .date(LocalDate.of(2026, 6, 15))
                .hour(LocalTime.of(15, 30))
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        when(parcheRepository.save(any(ParcheEntity.class))).thenReturn(parcheEntity);
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(MemberEntity.builder()
                .studentId(captainId)
                .memberRole(MemberRole.CAPTAIN)
                .build());

        ParcheResponse response = parcheService.createParche(req, captainId);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Parche de futbol");
        assertThat(response.getCaptainId()).isEqualTo(captainId);
        assertThat(response.getActualMembers()).isEqualTo(1);
        assertThat(response.getStatus()).isEqualTo(ParcheStatus.ACTIVE);

        verify(parcheRepository, times(1)).save(any(ParcheEntity.class));
        verify(memberRepository, times(1)).save(any(MemberEntity.class));
    }

    @Test
    void updateParche_shouldThrowAccessDeniedIfNotCaptain() {
        UUID otherUserId = UUID.randomUUID();
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .name("Nuevo nombre")
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));

        assertThatThrownBy(() -> parcheService.updateParche(parcheId, req, otherUserId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only the captain can edit this parche");

        verify(parcheRepository, never()).save(any());
    }

    @Test
    void getParcheById_shouldThrowNotFoundIfNotExists() {
        UUID randomId = UUID.randomUUID();
        when(parcheRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcheService.getParcheById(randomId))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessage("Parche not found with id: " + randomId);
    }

    @Test
    void getParcheById_shouldReturnParcheWhenExists() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);

        ParcheResponse response = parcheService.getParcheById(parcheId);

        assertThat(response.getId()).isEqualTo(parcheId);
        assertThat(response.getName()).isEqualTo("Parche de futbol");
        assertThat(response.getActualMembers()).isEqualTo(3);
    }

    @Test
    void updateParche_shouldUpdateFieldsWhenCaptainRequests() {
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .name("Nombre actualizado")
                .maximumQuota(20)
                .build();

        ParcheEntity updated = ParcheEntity.builder()
                .id(parcheId)
                .name("Nombre actualizado")
                .description("A jugar")
                .place("Cancha ECCI")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(20)
                .captainId(captainId)
                .dateRealization(LocalDateTime.of(2026, 6, 15, 15, 30))
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(parcheRepository.save(any(ParcheEntity.class))).thenReturn(updated);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        ParcheResponse response = parcheService.updateParche(parcheId, req, captainId);

        assertThat(response.getName()).isEqualTo("Nombre actualizado");
        assertThat(response.getMaximumQuota()).isEqualTo(20);
        verify(parcheRepository, times(1)).save(any(ParcheEntity.class));
    }

    @Test
    void deleteParche_shouldThrowAccessDeniedIfNotCaptain() {
        UUID otherUserId = UUID.randomUUID();
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));

        assertThatThrownBy(() -> parcheService.deleteParche(parcheId, otherUserId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Only the captain can delete this parche");

        verify(parcheRepository, never()).save(any());
    }

    @Test
    void deleteParche_shouldSetStatusToFiledWhenCaptainRequests() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(parcheRepository.save(any(ParcheEntity.class))).thenReturn(parcheEntity);

        parcheService.deleteParche(parcheId, captainId);

        assertThat(parcheEntity.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository, times(1)).save(parcheEntity);
    }

    @Test
    void getParches_shouldReturnAllParchesWhenNoFilters() {
        when(parcheRepository.findAll()).thenReturn(List.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        List<ParcheResponse> result = parcheService.getParches(null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
    }

    @Test
    void getParches_shouldFilterByTypeOnly() {
        when(parcheRepository.findByType(ParcheType.PUBLIC)).thenReturn(List.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        List<ParcheResponse> result = parcheService.getParches(ParcheType.PUBLIC, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(ParcheType.PUBLIC);
        verify(parcheRepository).findByType(ParcheType.PUBLIC);
    }

    @Test
    void getParches_shouldFilterByStatusOnly() {
        when(parcheRepository.findByStatus(ParcheStatus.ACTIVE)).thenReturn(List.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        List<ParcheResponse> result = parcheService.getParches(null, ParcheStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ParcheStatus.ACTIVE);
        verify(parcheRepository).findByStatus(ParcheStatus.ACTIVE);
    }

    @Test
    void getParches_shouldFilterByTypeAndStatus() {
        when(parcheRepository.findByTypeAndStatus(ParcheType.PUBLIC, ParcheStatus.ACTIVE))
                .thenReturn(List.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        List<ParcheResponse> result = parcheService.getParches(ParcheType.PUBLIC, ParcheStatus.ACTIVE);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByTypeAndStatus(ParcheType.PUBLIC, ParcheStatus.ACTIVE);
    }

    @Test
    void updateParche_shouldUpdateDateOnlyWhenOnlyDateProvided() {
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .date(LocalDate.of(2026, 8, 20))
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(parcheRepository.save(any(ParcheEntity.class))).thenReturn(parcheEntity);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        parcheService.updateParche(parcheId, req, captainId);

        verify(parcheRepository).save(argThat(p ->
                p.getDateRealization().toLocalDate().equals(LocalDate.of(2026, 8, 20))
        ));
    }

    @Test
    void updateParche_shouldUpdateHourOnlyWhenOnlyHourProvided() {
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .hour(LocalTime.of(18, 0))
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(parcheRepository.save(any(ParcheEntity.class))).thenReturn(parcheEntity);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);

        parcheService.updateParche(parcheId, req, captainId);

        verify(parcheRepository).save(argThat(p ->
                p.getDateRealization().toLocalTime().equals(LocalTime.of(18, 0))
        ));
    }

    @Test
    void validarCupoDisponible_shouldReturnTrueWhenSpaceAvailable() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(5);

        boolean result = parcheService.validarCupoDisponible(parcheId);

        assertThat(result).isTrue();
    }

    @Test
    void validarCupoDisponible_shouldReturnFalseWhenFull() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parcheEntity));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10);

        boolean result = parcheService.validarCupoDisponible(parcheId);

        assertThat(result).isFalse();
    }
}
