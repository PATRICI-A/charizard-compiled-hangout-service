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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheMapper parcheMapper;

    @InjectMocks UpdateParcheUseCase useCase;

    private UUID parcheId;
    private UUID captainId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        captainId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Original")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PUBLIC)
                .captainId(captainId)
                .build();
    }

    @Test
    @DisplayName("updateParche lanza ParcheNotFoundException cuando no existe")
    void updateParche_noExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.updateParche(parcheId, UpdateParcheRequest.builder().build(), captainId))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(parcheId.toString());
    }

    @Test
    @DisplayName("updateParche lanza AccessDeniedException cuando solicitante no es captain")
    void updateParche_noEsCaptain_lanzaAccessDenied() {
        UUID otroCaptain = UUID.randomUUID();
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        assertThatThrownBy(() -> useCase.updateParche(parcheId, UpdateParcheRequest.builder().build(), otroCaptain))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("updateParche actualiza nombre cuando se provee")
    void updateParche_conNombre_actualizaNombre() {
        UpdateParcheRequest req = UpdateParcheRequest.builder().name("Nuevo Nombre").build();
        ParcheResponse response = ParcheResponse.builder().id(parcheId).name("Nuevo Nombre").build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(response);

        ParcheResponse result = useCase.updateParche(parcheId, req, captainId);

        assertThat(parche.getName()).isEqualTo("Nuevo Nombre");
        assertThat(result.getName()).isEqualTo("Nuevo Nombre");
    }

    @Test
    @DisplayName("updateParche actualiza date y hour cuando se proveen")
    void updateParche_conDateYHour_actualizaDateYHour() {
        LocalDate date = LocalDate.of(2026, 8, 1);
        LocalTime hour = LocalTime.of(15, 0);
        UpdateParcheRequest req = UpdateParcheRequest.builder().date(date).hour(hour).build();
        ParcheResponse response = ParcheResponse.builder().id(parcheId).build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(response);

        useCase.updateParche(parcheId, req, captainId);

        assertThat(parche.getDate()).isEqualTo(date);
        assertThat(parche.getHour()).isEqualTo(hour);
    }

    @Test
    @DisplayName("updateParche no modifica campos nulos")
    void updateParche_camposNulos_noModificaValoresOriginales() {
        UpdateParcheRequest req = UpdateParcheRequest.builder().build();
        ParcheResponse response = ParcheResponse.builder().id(parcheId).name("Original").build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(response);

        useCase.updateParche(parcheId, req, captainId);

        assertThat(parche.getName()).isEqualTo("Original");
        assertThat(parche.getMaximumQuota()).isEqualTo(10);
    }

    @Test
    @DisplayName("updateParche actualiza todos los campos cuando se proveen")
    void updateParche_conTodosLosCampos_actualizaTodo() {
        UpdateParcheRequest req = UpdateParcheRequest.builder()
                .name("Nuevo")
                .description("Desc")
                .place("Lugar")
                .maximumQuota(20)
                .type(ParcheType.PRIVATE)
                .build();
        ParcheResponse response = ParcheResponse.builder().id(parcheId).build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(parcheRepository.save(parche)).thenReturn(parche);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(2);
        when(parcheMapper.toResponse(any(), anyInt())).thenReturn(response);

        useCase.updateParche(parcheId, req, captainId);

        assertThat(parche.getName()).isEqualTo("Nuevo");
        assertThat(parche.getDescription()).isEqualTo("Desc");
        assertThat(parche.getPlace()).isEqualTo("Lugar");
        assertThat(parche.getMaximumQuota()).isEqualTo(20);
        assertThat(parche.getType()).isEqualTo(ParcheType.PRIVATE);
    }
}
