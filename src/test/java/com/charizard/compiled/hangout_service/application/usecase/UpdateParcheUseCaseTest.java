package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.UpdateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.AccessDeniedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheMapper parcheMapper;

    @InjectMocks UpdateParcheUseCase useCase;

    private UUID parcheId;
    private UUID ownerId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Original")
                .description("Descripción original")
                .placeId(UUID.randomUUID())
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PUBLIC)
                .ownerId(ownerId)
                .build();
    }

    @Test
    @DisplayName("updateParche lanza ParcheNotFoundException cuando el parche no existe")
    void updateParche_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateParche(parcheId, UpdateParcheRequest.builder().build(), ownerId))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(parcheId.toString());
    }

    @Test
    @DisplayName("updateParche lanza AccessDeniedException cuando el solicitante no es el capitán")
    void updateParche_noEsCaptain_lanzaAccessDenied() {
        UUID otroCaptain = UUID.randomUUID();
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        assertThatThrownBy(() -> useCase.updateParche(parcheId, UpdateParcheRequest.builder().build(), otroCaptain))
                .isInstanceOf(AccessDeniedException.class);

        verify(parcheRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateParche no modifica el nombre (es inmutable)")
    void updateParche_nombreEsInmutable() {
        // name field no longer exists in UpdateParcheRequest — test that name stays unchanged
        UpdateParcheRequest req = UpdateParcheRequest.builder().description("Nueva desc").build();
        ParcheResponse response = ParcheResponse.builder().id(parcheId).name("Original").build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(response);

        ParcheResponse result = useCase.updateParche(parcheId, req, ownerId);

        assertThat(parche.getName()).isEqualTo("Original"); // name unchanged
        assertThat(result.getName()).isEqualTo("Original");
    }

    @Test
    @DisplayName("updateParche actualiza date y hour cuando se proveen")
    void updateParche_conDateYHour_actualizaAmbos() {
        LocalDate fecha = LocalDate.of(2026, 8, 1);
        LocalTime hora = LocalTime.of(15, 0);
        UpdateParcheRequest req = UpdateParcheRequest.builder().date(fecha).hour(hora).build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(ParcheResponse.builder().id(parcheId).build());

        useCase.updateParche(parcheId, req, ownerId);

        assertThat(parche.getDate()).isEqualTo(fecha);
        assertThat(parche.getHour()).isEqualTo(hora);
    }

    @Test
    @DisplayName("updateParche no modifica campos que llegan nulos en el request")
    void updateParche_camposNulos_noModificaValoresOriginales() {
        UpdateParcheRequest req = UpdateParcheRequest.builder().build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(ParcheResponse.builder().id(parcheId).build());

        useCase.updateParche(parcheId, req, ownerId);

        assertThat(parche.getName()).isEqualTo("Original");
        assertThat(parche.getMaximumQuota()).isEqualTo(10);
        assertThat(parche.getType()).isEqualTo(ParcheType.PUBLIC);
    }

    @Test
    @DisplayName("updateParche actualiza todos los campos cuando se proveen en el request")
    void updateParche_conTodosLosCampos_actualizaTodo() {
        UUID nuevoLugarId = UUID.randomUUID();
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .description("Nueva desc")
                .placeId(nuevoLugarId)
                .maximumQuota(20)
                .type(ParcheType.PRIVATE)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(2);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(ParcheResponse.builder().id(parcheId).build());

        useCase.updateParche(parcheId, req, ownerId);

        assertThat(parche.getName()).isEqualTo("Original"); // name is immutable
        assertThat(parche.getDescription()).isEqualTo("Nueva desc");
        assertThat(parche.getPlaceId()).isEqualTo(nuevoLugarId);
        assertThat(parche.getMaximumQuota()).isEqualTo(20);
        assertThat(parche.getType()).isEqualTo(ParcheType.PRIVATE);
    }
}
