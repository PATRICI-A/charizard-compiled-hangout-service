package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.InvitationStatus;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class InvitationEntityTest {

    @Autowired
    private InvitationRepository repository;

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
        UUID parcheId = UUID.randomUUID();
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
