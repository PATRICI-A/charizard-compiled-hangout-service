package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.adapter;

import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.mapper.MemberEntityMapper;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberRepositoryAdapterTest {

    @Mock MemberRepository memberRepository;
    @Mock MemberEntityMapper mapper;

    @InjectMocks MemberRepositoryAdapter adapter;

    private UUID parcheId;
    private UUID studentId;
    private Member member;
    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        member = Member.builder()
                .id(UUID.randomUUID())
                .parcheId(parcheId)
                .studentId(studentId)
                .memberRole(MemberRole.STUDENT)
                .build();
        memberEntity = new MemberEntity();
    }

    @Test
    @DisplayName("existsByParcheIdAndStudentId retorna true cuando el member existe")
    void existsByParcheIdAndStudentId_existe_retornaTrue() {
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(true);

        assertThat(adapter.existsByParcheIdAndStudentId(parcheId, studentId)).isTrue();
    }

    @Test
    @DisplayName("existsByParcheIdAndStudentId retorna false cuando el member no existe")
    void existsByParcheIdAndStudentId_noExiste_retornaFalse() {
        when(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId)).thenReturn(false);

        assertThat(adapter.existsByParcheIdAndStudentId(parcheId, studentId)).isFalse();
    }

    @Test
    @DisplayName("existsByParcheIdAndStudentIdAndMemberRole delega correctamente en el repositorio")
    void existsByParcheIdAndStudentIdAndMemberRole_delegaEnRepositorio() {
        when(memberRepository.existsByParcheIdAndStudentIdAndMemberRole(parcheId, studentId, MemberRole.CAPTAIN))
                .thenReturn(true);

        assertThat(adapter.existsByParcheIdAndStudentIdAndMemberRole(parcheId, studentId, MemberRole.CAPTAIN)).isTrue();
        verify(memberRepository).existsByParcheIdAndStudentIdAndMemberRole(parcheId, studentId, MemberRole.CAPTAIN);
    }

    @Test
    @DisplayName("countParchesActivosByStudentId delega al repositorio con status ACTIVE")
    void countParchesActivosByStudentId_delegaConStatusActive() {
        when(memberRepository.countParchesActivosByStudentIdAndStatus(studentId, ParcheStatus.ACTIVE)).thenReturn(3);

        assertThat(adapter.countParchesActivosByStudentId(studentId)).isEqualTo(3);
        verify(memberRepository).countParchesActivosByStudentIdAndStatus(studentId, ParcheStatus.ACTIVE);
    }

    @Test
    @DisplayName("countByParcheId retorna la cantidad correcta de miembros")
    void countByParcheId_retornaCuentaCorrecta() {
        when(memberRepository.countByParcheId(parcheId)).thenReturn(5);

        assertThat(adapter.countByParcheId(parcheId)).isEqualTo(5);
    }

    @Test
    @DisplayName("save convierte a entidad, persiste y retorna el dominio mapeado")
    void save_persisteEntidadYRetornaDominio() {
        when(mapper.toEntity(member)).thenReturn(memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(mapper.toDomain(memberEntity)).thenReturn(member);

        Member result = adapter.save(member);

        assertThat(result).isEqualTo(member);
        verify(mapper).toEntity(member);
        verify(memberRepository).save(memberEntity);
        verify(mapper).toDomain(memberEntity);
    }

    @Test
    @DisplayName("deleteByParcheIdAndStudentId delega la eliminación al repositorio")
    void deleteByParcheIdAndStudentId_delegaEliminar() {
        adapter.deleteByParcheIdAndStudentId(parcheId, studentId);

        verify(memberRepository).deleteByParcheIdAndStudentId(parcheId, studentId);
    }
}
