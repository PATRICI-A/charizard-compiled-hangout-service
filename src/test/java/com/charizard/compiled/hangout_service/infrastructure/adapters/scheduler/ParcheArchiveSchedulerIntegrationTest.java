package com.charizard.compiled.hangout_service.infrastructure.adapters.scheduler;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class ParcheArchiveSchedulerIntegrationTest {

    @Autowired ParcheArchiveScheduler scheduler;
    @Autowired ParcheRepository parcheRepository;

    @AfterEach
    void cleanup() {
        parcheRepository.deleteAll();
    }

    @Test
    @DisplayName("archiveExpiredParches archiva solo los parches con más de 24 horas de vencidos")
    void archiveExpiredParches_archivaSoloLosVencidosMasDe24h() {
        ParcheEntity vencido  = parcheRepository.save(buildParche(LocalDateTime.now().minusHours(25)));
        ParcheEntity reciente = parcheRepository.save(buildParche(LocalDateTime.now().minusHours(23)));
        ParcheEntity futuro   = parcheRepository.save(buildParche(LocalDateTime.now().plusHours(5)));

        scheduler.archiveExpiredParches();

        assertThat(parcheRepository.findById(vencido.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.FILED);
        assertThat(parcheRepository.findById(reciente.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.ACTIVE);
        assertThat(parcheRepository.findById(futuro.getId()).orElseThrow().getStatus())
                .isEqualTo(ParcheStatus.ACTIVE);
    }

    @Test
    @DisplayName("archiveExpiredParches no modifica parches cuando no hay expirados")
    void archiveExpiredParches_sinVencidos_noModificaNada() {
        ParcheEntity reciente = parcheRepository.save(buildParche(LocalDateTime.now().minusHours(1)));
        ParcheEntity futuro   = parcheRepository.save(buildParche(LocalDateTime.now().plusDays(1)));

        scheduler.archiveExpiredParches();

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
                .status(ParcheStatus.ACTIVE)
                .captainId(UUID.randomUUID())
                .build();
    }
}
