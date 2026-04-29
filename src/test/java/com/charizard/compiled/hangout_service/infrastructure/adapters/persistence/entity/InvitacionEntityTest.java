package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.InvitacionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InvitacionEntityTest {

    @Autowired
    private InvitacionRepository repository;

    private InvitacionEntity buildValidInvitacion(UUID parcheId, UUID estudianteId) {
        return InvitacionEntity.builder()
                .parcheId(parcheId)
                .capitanId(UUID.randomUUID())
                .estudianteInvitadoId(estudianteId)
                .estado(EstadoInvitacion.PENDIENTE)
                .build();
    }

    @Test
    void shouldFailWhenDuplicateInvitation() {
        UUID parcheId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        repository.save(buildValidInvitacion(parcheId, estudianteId));
        repository.flush();

        InvitacionEntity duplicate = buildValidInvitacion(parcheId, estudianteId);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(duplicate);
            repository.flush();
        });
    }
}
