package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.request.CreateParcheRequest;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheMapper parcheMapper;
    @Mock ParcheEventPublisherPort parcheEventPublisher;

    @InjectMocks CreateParcheUseCase useCase;

    private UUID ownerId;
    private CreateParcheRequest request;
    private Parche domainParche;
    private ParcheResponse parcheResponse;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();

        request = CreateParcheRequest.builder()
                .name("Parche de estudio")
                .lugar("ED_A")
                .maximumQuota(10)
                .type(ParcheType.PUBLIC)
                .build();

        domainParche = Parche.builder()
                .id(UUID.randomUUID())
                .name("Parche de estudio")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .ownerId(ownerId)
                .build();

        parcheResponse = ParcheResponse.builder()
                .id(domainParche.getId())
                .name("Parche de estudio")
                .actualMembers(1)
                .build();
    }

    @Test
    @DisplayName("createParche con menos de 5 activos crea parche y retorna respuesta")
    void createParche_menosDe5Activos_creaYRetorna() {
        when(memberRepository.countParchesActivosByStudentId(ownerId)).thenReturn(4);
        when(parcheMapper.toDomain(request, ownerId)).thenReturn(domainParche);
        when(parcheRepository.save(domainParche)).thenReturn(domainParche);
        when(parcheMapper.toResponse(domainParche, 1)).thenReturn(parcheResponse);

        ParcheResponse result = useCase.createParche(request, ownerId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(domainParche.getId());
        assertThat(result.getName()).isEqualTo("Parche de estudio");
    }

    @Test
    @DisplayName("createParche con 0 activos permite crear")
    void createParche_ceroActivos_permiteCrear() {
        when(memberRepository.countParchesActivosByStudentId(ownerId)).thenReturn(0);
        when(parcheMapper.toDomain(request, ownerId)).thenReturn(domainParche);
        when(parcheRepository.save(any())).thenReturn(domainParche);
        when(parcheMapper.toResponse(domainParche, 1)).thenReturn(parcheResponse);

        ParcheResponse result = useCase.createParche(request, ownerId);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("createParche con 5 activos lanza MaxHangoutsReachedException y no persiste")
    void createParche_con5Activos_lanzaExcepcionSinPersistir() {
        when(memberRepository.countParchesActivosByStudentId(ownerId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.createParche(request, ownerId))
                .isInstanceOf(MaxHangoutsReachedException.class);

        verify(parcheRepository, never()).save(any());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("createParche guarda member con studentId y parcheId correctos (sin memberRole)")
    void createParche_guardaMemberOwnerConDatosCorrectos() {
        when(memberRepository.countParchesActivosByStudentId(ownerId)).thenReturn(0);
        when(parcheMapper.toDomain(request, ownerId)).thenReturn(domainParche);
        when(parcheRepository.save(domainParche)).thenReturn(domainParche);
        when(parcheMapper.toResponse(domainParche, 1)).thenReturn(parcheResponse);

        useCase.createParche(request, ownerId);

        verify(memberRepository).save(argThat(m ->
                m.getStudentId().equals(ownerId) &&
                m.getParcheId().equals(domainParche.getId())
        ));
    }

    @Test
    @DisplayName("createParche guarda el parche antes de guardar el member")
    void createParche_guardaParcheAntesQueElMember() {
        when(memberRepository.countParchesActivosByStudentId(ownerId)).thenReturn(0);
        when(parcheMapper.toDomain(request, ownerId)).thenReturn(domainParche);
        when(parcheRepository.save(domainParche)).thenReturn(domainParche);
        when(parcheMapper.toResponse(domainParche, 1)).thenReturn(parcheResponse);

        useCase.createParche(request, ownerId);

        var inOrder = inOrder(parcheRepository, memberRepository);
        inOrder.verify(parcheRepository).save(domainParche);
        inOrder.verify(memberRepository).save(any(Member.class));
    }
}
