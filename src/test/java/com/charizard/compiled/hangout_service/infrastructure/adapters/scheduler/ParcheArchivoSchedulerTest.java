package com.charizard.compiled.hangout_service.infrastructure.adapters.scheduler;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ParcheArchivoSchedulerTest {

    @Autowired
    private ParcheArchiveScheduler scheduler;

    @Autowired
    private ParcheRepository parcheRepository;

    @AfterEach
    void cleanup() {
        parcheRepository.deleteAll();
    }

    @Test
    void soloDebeArchivarParchesConMasDe24HorasDeAntiguedad() {
        ParcheEntity vencido = buildParche(LocalDateTime.now().minusHours(25));
        ParcheEntity reciente = buildParche(LocalDateTime.now().minusHours(23));
        ParcheEntity futuro = buildParche(LocalDateTime.now().plusHours(5));

        parcheRepository.save(vencido);
        parcheRepository.save(reciente);
        parcheRepository.save(futuro);

        scheduler.archiveExpiredParches();

        assertThat(parcheRepository.findById(vencido.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.FILED);
        assertThat(parcheRepository.findById(reciente.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.ACTIVE);
        assertThat(parcheRepository.findById(futuro.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.ACTIVE);
    }

    private ParcheEntity buildParche(LocalDateTime dateRealization) {
        return ParcheEntity.builder()
                .name("Parche test")
                .place("Parque")
                .category(ParcheCategory.CINEMA)
                .type(ParcheType.PUBLIC)
                .date(dateRealization.toLocalDate())
                .hour(dateRealization.toLocalTime())
                .maximumQuota(10)
                .dateRealization(dateRealization)
                .status(ParcheStatus.ACTIVE)
                .captainId(UUID.randomUUID())
                .build();
    }
}
