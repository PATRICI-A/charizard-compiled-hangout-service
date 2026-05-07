package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheMapper parcheMapper;

    @InjectMocks GetParcheUseCase useCase;

    private UUID parcheId;
    private Parche parche;
    private ParcheResponse parcheResponse;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche de fútbol")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .date(LocalDate.of(2026, 6, 15))
                .dateRealization(LocalDateTime.of(2026, 6, 15, 15, 30))
                .captainId(UUID.randomUUID())
                .build();

        parcheResponse = ParcheResponse.builder()
                .id(parcheId)
                .name("Parche de fútbol")
                .type(ParcheType.PUBLIC)
                .status(ParcheStatus.ACTIVE)
                .maximumQuota(10)
                .actualMembers(3)
                .build();
    }

    @Test
    @DisplayName("getParches sin filtros retorna todos los parches")
    void getParches_sinFiltros_retornaTodos() {
        when(parcheRepository.findByFilters(null, null, null, null)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
        verify(parcheRepository).findByFilters(null, null, null, null);
    }

    @Test
    @DisplayName("getParches con nombre pasa el filtro al repositorio")
    void getParches_conNombre_pasaFiltroAlRepositorio() {
        when(parcheRepository.findByFilters(null, null, "fútbol", null)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, "fútbol", null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters(null, null, "fútbol", null);
    }

    @Test
    @DisplayName("getParches con fecha pasa el filtro al repositorio")
    void getParches_conFecha_pasaFiltroAlRepositorio() {
        LocalDate fecha = LocalDate.of(2026, 6, 15);
        when(parcheRepository.findByFilters(null, null, null, fecha)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, null, fecha, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters(null, null, null, fecha);
    }

    @Test
    @DisplayName("getParches con cupoDisponible=true incluye solo parches con espacio")
    void getParches_cupoDisponibleTrue_soloParchesConEspacio() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder()
                .id(idLleno)
                .name("Parche lleno")
                .maximumQuota(2)
                .status(ParcheStatus.ACTIVE)
                .build();

        when(parcheRepository.findByFilters(null, null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);  // 3/10 → hay espacio
        when(memberRepository.countByParcheId(idLleno)).thenReturn(2);   // 2/2 → lleno
        when(parcheMapper.toResponse(eq(parche), anyInt())).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("getParches con cupoDisponible=false incluye solo parches llenos")
    void getParches_cupoDisponibleFalse_soloParchesLlenos() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder()
                .id(idLleno)
                .name("Parche lleno")
                .maximumQuota(2)
                .status(ParcheStatus.ACTIVE)
                .build();
        ParcheResponse responseLleno = ParcheResponse.builder()
                .id(idLleno).name("Parche lleno").maximumQuota(2).actualMembers(2).build();

        when(parcheRepository.findByFilters(null, null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);  // 3/10 → hay espacio
        when(memberRepository.countByParcheId(idLleno)).thenReturn(2);   // 2/2 → lleno
        when(parcheMapper.toResponse(eq(parcheLleno), anyInt())).thenReturn(responseLleno);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(idLleno);
    }

    @Test
    @DisplayName("getParches con cupoDisponible=null retorna todos sin filtrar por cupo")
    void getParches_cupoDisponibleNull_retornaTodos() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder()
                .id(idLleno).name("Parche lleno").maximumQuota(2).build();
        ParcheResponse responseLleno = ParcheResponse.builder().id(idLleno).build();

        when(parcheRepository.findByFilters(null, null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(memberRepository.countByParcheId(idLleno)).thenReturn(2);
        when(parcheMapper.toResponse(eq(parche), anyInt())).thenReturn(parcheResponse);
        when(parcheMapper.toResponse(eq(parcheLleno), anyInt())).thenReturn(responseLleno);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getParches con tipo y estado pasa ambos filtros al repositorio")
    void getParches_conTipoYEstado_pasaAmbosAlRepositorio() {
        when(parcheRepository.findByFilters(ParcheType.PUBLIC, ParcheStatus.ACTIVE, null, null))
                .thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(ParcheType.PUBLIC, ParcheStatus.ACTIVE, null, null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters(ParcheType.PUBLIC, ParcheStatus.ACTIVE, null, null);
    }

    @Test
    @DisplayName("getParcheById retorna el parche cuando existe")
    void getParcheById_retornaParche_cuandoExiste() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        ParcheResponse result = useCase.getParcheById(parcheId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("getParcheById lanza ParcheNotFoundException cuando no existe")
    void getParcheById_lanzaExcepcion_cuandoNoExiste() {
        UUID inexistente = UUID.randomUUID();
        when(parcheRepository.findById(inexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getParcheById(inexistente))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(inexistente.toString());
    }
}
