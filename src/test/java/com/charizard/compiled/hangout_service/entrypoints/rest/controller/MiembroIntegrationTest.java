package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.ParcheEntity;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.MemberRepository;
import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository.ParcheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MiembroIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParcheRepository parcheRepository;

    @Autowired
    private MemberRepository memberRepository;

    private UUID captainId;
    private UUID parcheId;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        parcheRepository.deleteAll();

        captainId = UUID.randomUUID();

        ParcheEntity parche = ParcheEntity.builder()
                .name("Parche Base")
                .description("Parche para tests de integración")
                .place("Campus universitario")
                .type(ParcheType.PUBLIC)
                .maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE)
                .captainId(captainId)
                .build();
        parcheId = parcheRepository.save(parche).getId();

        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId)
                .studentId(captainId)
                .memberRole(MemberRole.CAPTAIN)
                .build());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flujo: Unirse a parche público
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void unirse_flujoExitoso() {
        UUID studentId = UUID.randomUUID();

        ResponseEntity<Void> response = doPost(parcheId, studentId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void unirse_cupoLleno_retorna409() {
        ParcheEntity fullParche = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Lleno").description("Sin cupo").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(2)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.ACTIVE).captainId(captainId)
                .build());

        memberRepository.save(MemberEntity.builder().parcheId(fullParche.getId())
                .studentId(UUID.randomUUID()).memberRole(MemberRole.CAPTAIN).build());
        memberRepository.save(MemberEntity.builder().parcheId(fullParche.getId())
                .studentId(UUID.randomUUID()).memberRole(MemberRole.STUDENT).build());

        ResponseEntity<Void> response = doPost(fullParche.getId(), UUID.randomUUID());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void unirse_masde5ParachesActivos_retorna409() {
        UUID busyStudent = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            ParcheEntity other = parcheRepository.save(ParcheEntity.builder()
                    .name("Parche extra " + i).description("Desc").place("Lugar")
                    .type(ParcheType.PUBLIC).maximumQuota(10)
                    .dateRealization(LocalDateTime.now().plusDays(1))
                    .status(ParcheStatus.ACTIVE).captainId(UUID.randomUUID())
                    .build());
            memberRepository.save(MemberEntity.builder()
                    .parcheId(other.getId()).studentId(busyStudent)
                    .memberRole(MemberRole.STUDENT).build());
        }

        ResponseEntity<Void> response = doPost(parcheId, busyStudent);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void unirse_estudianteYaEsMiembro_retorna409() {
        UUID studentId = UUID.randomUUID();
        doPost(parcheId, studentId); // primer intento OK

        ResponseEntity<Void> response = doPost(parcheId, studentId); // segundo intento

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void unirse_parcheNoExiste_retorna404() {
        ResponseEntity<Void> response = doPost(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void unirse_parcheArchivado_retorna400() {
        ParcheEntity archived = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.FILED).captainId(captainId)
                .build());

        ResponseEntity<Void> response = doPost(archived.getId(), UUID.randomUUID());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Flujo: Salir del parche
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void salir_flujoExitoso() {
        UUID studentId = UUID.randomUUID();
        memberRepository.save(MemberEntity.builder()
                .parcheId(parcheId).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build());

        ResponseEntity<Void> response = doDelete(parcheId, studentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(memberRepository.existsByParcheIdAndStudentId(parcheId, studentId));
    }

    @Test
    void salir_capitan_sinTransferencia_retorna400() {
        ResponseEntity<Void> response = doDelete(parcheId, captainId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(memberRepository.existsByParcheIdAndStudentId(parcheId, captainId));
    }

    @Test
    void salir_noEsMiembro_retorna404() {
        ResponseEntity<Void> response = doDelete(parcheId, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void salir_parcheArchivado_retorna400() {
        ParcheEntity archived = parcheRepository.save(ParcheEntity.builder()
                .name("Parche Archivado").description("Archivado").place("Lugar")
                .type(ParcheType.PUBLIC).maximumQuota(10)
                .dateRealization(LocalDateTime.now().plusDays(1))
                .status(ParcheStatus.FILED).captainId(captainId)
                .build());

        UUID studentId = UUID.randomUUID();
        memberRepository.save(MemberEntity.builder()
                .parcheId(archived.getId()).studentId(studentId)
                .memberRole(MemberRole.STUDENT).build());

        ResponseEntity<Void> response = doDelete(archived.getId(), studentId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private ResponseEntity<Void> doPost(UUID parcheId, UUID studentId) {
        return restTemplate.exchange(
                url("/api/v1/parches/" + parcheId + "/miembros"),
                HttpMethod.POST,
                withUserId(studentId),
                Void.class);
    }

    private ResponseEntity<Void> doDelete(UUID parcheId, UUID studentId) {
        return restTemplate.exchange(
                url("/api/v1/parches/" + parcheId + "/miembros"),
                HttpMethod.DELETE,
                withUserId(studentId),
                Void.class);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<Void> withUserId(UUID userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", userId.toString());
        return new HttpEntity<>(headers);
    }
}
