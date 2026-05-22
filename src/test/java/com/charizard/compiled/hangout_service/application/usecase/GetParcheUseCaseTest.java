package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.ParcheDetailResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ParcheResponse;
import com.charizard.compiled.hangout_service.application.mapper.ParcheMapper;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.PlaceServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
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
    @Mock PlaceServicePort placeServicePort;

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
                .hour(LocalTime.of(15, 30))
                .ownerId(UUID.randomUUID())
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

    // ─── getParches ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getParches sin filtros pasa todos los filtros como null al repositorio")
    void getParches_sinFiltros_pasaNullsAlRepositorio() {
        when(parcheRepository.findByFilters(null, null, null)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
        verify(parcheRepository).findByFilters(null, null, null);
    }

    @Test
    @DisplayName("getParches con nombre pasa el filtro de nombre al repositorio")
    void getParches_conNombre_pasaNombreAlRepositorio() {
        when(parcheRepository.findByFilters("fútbol", null, null)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches("fútbol", null, null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters("fútbol", null, null);
    }

    @Test
    @DisplayName("getParches con fecha pasa el filtro de fecha al repositorio")
    void getParches_conFecha_pasaFechaAlRepositorio() {
        LocalDate fecha = LocalDate.of(2026, 6, 15);
        when(parcheRepository.findByFilters(null, fecha, null)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, fecha, null, null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters(null, fecha, null);
    }

    @Test
    @DisplayName("getParches con categoria pasa el filtro de categoria al repositorio")
    void getParches_conCategoria_pasaCategoriaAlRepositorio() {
        when(parcheRepository.findByFilters(null, null, "Deportes")).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, "Deportes", null);

        assertThat(result).hasSize(1);
        verify(parcheRepository).findByFilters(null, null, "Deportes");
    }

    @Test
    @DisplayName("getParches con cupoDisponible=true incluye solo parches con espacio disponible")
    void getParches_cupoDisponibleTrue_incluyeSoloParchesConEspacio() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder().id(idLleno).name("Parche lleno").maximumQuota(2).build();

        when(parcheRepository.findByFilters(null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parche.getId())).thenReturn(3);    // 3/10 → hay espacio
        when(memberRepository.countByParcheId(parcheLleno.getId())).thenReturn(2); // 2/2 → lleno
        when(parcheMapper.toResponse(eq(parche), anyInt())).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getParches(null, null, null, true);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("getParches con cupoDisponible=false incluye solo parches llenos")
    void getParches_cupoDisponibleFalse_incluyeSoloParchesLlenos() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder().id(idLleno).name("Parche lleno").maximumQuota(2).build();
        ParcheResponse responseLleno = ParcheResponse.builder().id(idLleno).maximumQuota(2).actualMembers(2).build();

        when(parcheRepository.findByFilters(null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parche.getId())).thenReturn(3);       // 3/10 → hay espacio
        when(memberRepository.countByParcheId(parcheLleno.getId())).thenReturn(2);  // 2/2 → lleno
        when(parcheMapper.toResponse(eq(parcheLleno), anyInt())).thenReturn(responseLleno);

        List<ParcheResponse> result = useCase.getParches(null, null, null, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(idLleno);
    }

    @Test
    @DisplayName("getParches con cupoDisponible=null retorna todos sin filtrar por cupo")
    void getParches_cupoDisponibleNull_retornaTodosSinFiltrar() {
        UUID idLleno = UUID.randomUUID();
        Parche parcheLleno = Parche.builder().id(idLleno).name("Parche lleno").maximumQuota(2).build();
        ParcheResponse responseLleno = ParcheResponse.builder().id(idLleno).build();

        when(parcheRepository.findByFilters(null, null, null))
                .thenReturn(List.of(parche, parcheLleno));
        when(memberRepository.countByParcheId(parche.getId())).thenReturn(3);
        when(memberRepository.countByParcheId(parcheLleno.getId())).thenReturn(2);
        when(parcheMapper.toResponse(eq(parche), anyInt())).thenReturn(parcheResponse);
        when(parcheMapper.toResponse(eq(parcheLleno), anyInt())).thenReturn(responseLleno);

        List<ParcheResponse> result = useCase.getParches(null, null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("getParcheById lanza ParcheNotFoundException cuando no existe")
    void getParcheById_parcheNoExiste_lanzaExcepcion() {
        UUID inexistente = UUID.randomUUID();
        when(parcheRepository.findById(inexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getParcheById(inexistente))
                .isInstanceOf(ParcheNotFoundException.class)
                .hasMessageContaining(inexistente.toString());
    }

    // ─── getMyParches ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getMyParches retorna los parches activos donde el usuario es miembro")
    void getMyParches_usuarioConParches_retornaLista() {
        UUID userId = UUID.randomUUID();
        List<UUID> parcheIds = List.of(parcheId);

        when(memberRepository.findParcheIdsByStudentId(userId)).thenReturn(parcheIds);
        when(parcheRepository.findActiveByIds(parcheIds)).thenReturn(List.of(parche));
        when(memberRepository.countByParcheId(parcheId)).thenReturn(3);
        when(parcheMapper.toResponse(parche, 3)).thenReturn(parcheResponse);

        List<ParcheResponse> result = useCase.getMyParches(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(parcheId);
    }

    @Test
    @DisplayName("getMyParches retorna lista vacía cuando el usuario no pertenece a ningún parche")
    void getMyParches_usuarioSinParches_retornaListaVacia() {
        UUID userId = UUID.randomUUID();

        when(memberRepository.findParcheIdsByStudentId(userId)).thenReturn(List.of());
        when(parcheRepository.findActiveByIds(List.of())).thenReturn(List.of());

        List<ParcheResponse> result = useCase.getMyParches(userId);

        assertThat(result).isEmpty();
    }
}
