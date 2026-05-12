package com.charizard.compiled.hangout_service.infrastructure.adapters.scheduler;

import com.charizard.compiled.hangout_service.domain.ports.in.ArchiveParcheInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler programado que ejecuta el archivado automático de parches vencidos.
 * Se ejecuta cada hora (cron: "0 0 * * * *") y archiva los parches cuya
 * fecha de realización superó las 24 horas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParcheArchiveScheduler {

    private final ArchiveParcheInputPort archiveParcheUseCase;

    @Scheduled(cron = "0 0 * * * *")
    public void archiveExpiredParches() {
        int archived = archiveParcheUseCase.archiveExpired();
        log.info("Scheduled archive job completed: {} parche(s) archived", archived);
    }
}
