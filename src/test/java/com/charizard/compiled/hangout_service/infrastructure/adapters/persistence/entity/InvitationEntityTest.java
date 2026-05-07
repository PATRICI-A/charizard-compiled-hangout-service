package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitationRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class InvitationEntityTest {

    @Autowired
    private InvitationRepository repository;

    @Autowired
    private ParcheRepository parcheRepository;

    @AfterEach
    void cleanup() {
        repository.deleteAll();
        parcheRepository.deleteAll();
    }

    private ParcheEntity buildParche() {
        LocalDateTime date = LocalDateTime.now().plusDays(1);
        return ParcheEntity.builder()
                .name("Parche test")
                .place("Lugar")
                .type(ParcheType.PUBLIC)
                .date(date.toLocalDate())
                .hour(date.toLocalTime())
                .maximumQuota(10)
                .dateRealization(date)
                .status(ParcheStatus.ACTIVE)
                .captainId(UUID.randomUUID())
                .build();
    }

    private InvitationEntity buildValidInvitation(UUID parcheId, UUID studentId) {
        return InvitationEntity.builder()
                .parcheId(parcheId)
                .captainId(UUID.randomUUID())
                .invitedStudentId(studentId)
                .status(InvitationStatus.PENDING)
                .build();
    }

    @Test
    void shouldFailWhenDuplicateInvitation() {
        UUID parcheId = parcheRepository.save(buildParche()).getId();
        UUID studentId = UUID.randomUUID();

        repository.save(buildValidInvitation(parcheId, studentId));
        repository.flush();

        InvitationEntity duplicate = buildValidInvitation(parcheId, studentId);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(duplicate);
            repository.flush();
        });
    }
}
