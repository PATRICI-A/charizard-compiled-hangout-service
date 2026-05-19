package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.exceptions.ParcheNotFoundException;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseParcheUseCaseTest {

    @Mock ParcheRepositoryPort parcheRepository;
    @Mock MemberRepositoryPort memberRepository;
    @Mock ParcheEventPublisherPort parcheEventPublisher;

    @InjectMocks CloseParcheUseCase useCase;

    private UUID parcheId;
    private Parche parche;

    @BeforeEach
    void setUp() {
        parcheId = UUID.randomUUID();

        parche = Parche.builder()
                .id(parcheId)
                .name("Parche Test")
                .ownerId(UUID.randomUUID())
                .status(ParcheStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("closeParche cambia el status a FILED, guarda y publica parche.dissolved")
    void closeParche_adminPuede_cambiaStatusYGuarda() {
        UUID miembro1 = UUID.randomUUID();
        UUID miembro2 = UUID.randomUUID();

        Member m1 = Member.builder().studentId(miembro1).parcheId(parcheId).build();
        Member m2 = Member.builder().studentId(miembro2).parcheId(parcheId).build();

        when(parcheRepository.findById(parcheId)).thenReturn(Optional.of(parche));
        when(memberRepository.findByParcheId(parcheId)).thenReturn(List.of(m1, m2));

        useCase.closeParche(parcheId);

        assertThat(parche.getStatus()).isEqualTo(ParcheStatus.FILED);
        verify(parcheRepository).save(parche);
        verify(parcheEventPublisher).publishParcheDissolved(
                eq(parcheId), eq("Parche Test"), eq(List.of(miembro1, miembro2)));
    }
}
