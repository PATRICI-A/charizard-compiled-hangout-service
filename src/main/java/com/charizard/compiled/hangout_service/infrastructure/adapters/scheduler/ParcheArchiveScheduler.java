package com.charizard.compiled.hangout_service.infrastructure.adapters.scheduler;

import com.charizard.compiled.hangout_service.domain.ports.in.ArchiveParcheInputPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
