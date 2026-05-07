package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheMapper parcheMapper;

    @InjectMocks CreateParcheUseCase useCase;

    private UUID captainId;
    private CreateParcheRequest request;
    private Parche savedParche;
    private ParcheResponse parcheResponse;

    @BeforeEach
    void setUp() {
        captainId = UUID.randomUUID();
        request = CreateParcheRequest.builder()
                .name("Parche de estudio")
                .place("Biblioteca")
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        savedParche = Parche.builder()
                .id(UUID.randomUUID())
                .name("Parche de estudio")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();

        parcheResponse = ParcheResponse.builder()
                .id(savedParche.getId())
                .name("Parche de estudio")
                .actualMembers(1)
                .build();
    }

    @Test
    @DisplayName("createParche con menos de 5 activos crea parche y member captain")
    void createParche_conMenosDe5Activos_creaParche() {
        when(memberRepository.countParchesActivosByStudentId(captainId)).thenReturn(4);
        when(parcheMapper.toDomain(request, captainId)).thenReturn(savedParche);
        when(parcheRepository.save(savedParche)).thenReturn(savedParche);
        when(parcheMapper.toResponse(savedParche, 1)).thenReturn(parcheResponse);

        ParcheResponse result = useCase.createParche(request, captainId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedParche.getId());
        verify(memberRepository).save(any(Member.class));
        verify(parcheRepository).save(savedParche);
    }

    @Test
    @DisplayName("createParche con 5 activos lanza MaxHangoutsReachedException")
    void createParche_con5Activos_lanzaExcepcion() {
        when(memberRepository.countParchesActivosByStudentId(captainId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.createParche(request, captainId))
                .isInstanceOf(MaxHangoutsReachedException.class);

        verify(parcheRepository, never()).save(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("createParche guarda member con rol CAPTAIN y studentId correcto")
    void createParche_guardaMemberConRolCaptain() {
        when(memberRepository.countParchesActivosByStudentId(captainId)).thenReturn(0);
        when(parcheMapper.toDomain(request, captainId)).thenReturn(savedParche);
        when(parcheRepository.save(savedParche)).thenReturn(savedParche);
        when(parcheMapper.toResponse(savedParche, 1)).thenReturn(parcheResponse);

        useCase.createParche(request, captainId);

        verify(memberRepository).save(argThat(m ->
                m.getMemberRole() == MemberRole.CAPTAIN &&
                m.getStudentId().equals(captainId) &&
                m.getParcheId().equals(savedParche.getId())
        ));
    }

    @Test
    @DisplayName("createParche con 0 activos permite crear")
    void createParche_con0Activos_permiteCrear() {
        when(memberRepository.countParchesActivosByStudentId(captainId)).thenReturn(0);
        when(parcheMapper.toDomain(request, captainId)).thenReturn(savedParche);
        when(parcheRepository.save(any())).thenReturn(savedParche);
        when(parcheMapper.toResponse(savedParche, 1)).thenReturn(parcheResponse);

        ParcheResponse result = useCase.createParche(request, captainId);

        assertThat(result).isNotNull();
    }
}
