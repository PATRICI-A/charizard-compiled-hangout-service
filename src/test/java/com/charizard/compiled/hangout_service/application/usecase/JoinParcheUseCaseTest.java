package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.MemberResponse;
import com.charizard.compiled.hangout_service.domain.exceptions.MaxHangoutsReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.MaximumCapacityReachedException;
import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.exceptions.StudentAlreadyMemberException;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;

    @InjectMocks JoinParcheUseCase useCase;

    private UUID parcheId;
    private UUID studentId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Activo")
                .maximumQuota(10)
                .status(ParcheStatus.ACTIVE)
                .type(ParcheType.PUBLIC)
                .captainId(UUID.randomUUID())
                .build();
    }

    @Test
    @DisplayName("unirseAParche lanza ParcheNotFoundException cuando el parche no existe")
    void unirseAParche_parcheNoExiste_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.unirseAParche(parcheId, studentId))
                .isInstanceOf(ParcheNotFoundException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirseAParche lanza IllegalArgumentException cuando el parche está archivado")
    void unirseAParche_parcheArchivado_lanzaIllegalArgument() {
        parche.setStatus(ParcheStatus.FILED);
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));

        assertThatThrownBy(() -> useCase.unirseAParche(parcheId, studentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("archived");

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirseAParche lanza StudentAlreadyMemberException cuando el student ya es miembro")
    void unirseAParche_yaMiembro_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.unirseAParche(parcheId, studentId))
                .isInstanceOf(StudentAlreadyMemberException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirseAParche lanza MaximumCapacityReachedException cuando el cupo está lleno")
    void unirseAParche_cupoLleno_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(10);

        assertThatThrownBy(() -> useCase.unirseAParche(parcheId, studentId))
                .isInstanceOf(MaximumCapacityReachedException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirseAParche lanza MaxHangoutsReachedException cuando el student tiene 5 parches activos")
    void unirseAParche_estudianteCon5Activos_lanzaExcepcion() {
        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(5);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(5);

        assertThatThrownBy(() -> useCase.unirseAParche(parcheId, studentId))
                .isInstanceOf(MaxHangoutsReachedException.class);

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("unirseAParche guarda member con rol STUDENT cuando todas las validaciones pasan")
    void unirseAParche_valido_guardaMemberConRolStudent() {
        Member savedMember = Member.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .studentId(studentId)
                .memberRole(MemberRole.STUDENT)
                .build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(5);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(4);
        when(memberRepository.save(any())).thenReturn(savedMember);

        MemberResponse result = useCase.unirseAParche(parcheId, studentId);

        assertThat(result).isNotNull();
        assertThat(result.getParcheId()).isEqualTo(parcheId);
        assertThat(result.getStudentId()).isEqualTo(studentId);
        assertThat(result.getMemberRole()).isEqualTo(MemberRole.STUDENT);
    }

    @Test
    @DisplayName("unirseAParche permite unirse cuando el student tiene exactamente 4 activos")
    void unirseAParche_estudianteCon4Activos_permiteUnirse() {
        Member savedMember = Member.builder()
                .id(UUID.randomUUID()).parcheId(parcheId).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);
        when(memberRepository.countByParcheId(parcheId)).thenReturn(1);
        when(memberRepository.countParchesActivosByStudentId(studentId)).thenReturn(4);
        when(memberRepository.save(any())).thenReturn(savedMember);

        assertThatCode(() -> useCase.unirseAParche(parcheId, studentId))
                .doesNotThrowAnyException();
    }
}
