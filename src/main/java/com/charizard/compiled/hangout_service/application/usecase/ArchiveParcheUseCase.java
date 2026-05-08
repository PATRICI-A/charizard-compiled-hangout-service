package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.ports.in.ArchiveParcheInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ArchiveParcheUseCase implements ArchiveParcheInputPort {

    private final ParcheRepositoryPort parcheRepository;

    @Override
    public int archiveExpired() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<Parche> expired = parcheRepository.findArchivables(
                ParcheStatus.ACTIVE, threshold.toLocalDate(), threshold.toLocalTime());

        expired.forEach(parche -> {
            parche.setStatus(ParcheStatus.FILED);
            parcheRepository.save(parche);
        });

        return expired.size();
    }
}
