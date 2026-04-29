package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.EnviarInvitacionResponse;
import com.charizard.compiled.hangout_service.application.dto.response.ErrorResponse;
import com.charizard.compiled.hangout_service.application.dto.response.InvitacionResponse;
import com.charizard.compiled.hangout_service.domain.ports.in.InvitacionInputPort;
import com.charizard.compiled.hangout_service.domain.events.InvitacionEnviadaEvent;
import com.charizard.compiled.hangout_service.domain.exceptions.EstudianteYaEsMiembroException;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitacionDuplicadaException;
import com.charizard.compiled.hangout_service.domain.model.Invitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitacionRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para el flujo de invitaciones.
 * Implementa la lógica de negocio y orquesta los puertos de salida.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InvitacionUseCase implements InvitacionInputPort {

    private final InvitacionRepositoryPort invitacionRepository;
    private final MemberRepositoryPort memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Envía invitaciones a múltiples estudiantes para un parche.
     * Valida que el capitán sea el dueño del parche, que los estudiantes no sean ya miembros
     * y que no tengan invitaciones pendientes.
     *
     * @param parcheId ID del parche
     * @param capitanId ID del capitán
     * @param estudiantesIds lista de estudiantes a invitar
     * @return respuesta con invitaciones creadas y posibles errores
     */
    @Override
    public EnviarInvitacionResponse enviarInvitacion(UUID parcheId, UUID capitanId, List<UUID> estudiantesIds) {
        List<Invitacion> invitacionesCreadas = new ArrayList<>();
        List<ErrorResponse> errores = new ArrayList<>();

        boolean esCapitan = memberRepository.existsByParcheIdAndStudentIdAndMemberRole(
                parcheId, capitanId, MemberRole.CAPTAIN);

        if (!esCapitan) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no es capitán de este parche"
            );
        }

        for (UUID estudianteId : estudiantesIds) {
            try {
                boolean yaEsMiembro = memberRepository.existsByParcheIdAndStudentId(parcheId, estudianteId);
                if (yaEsMiembro) {
                    throw new EstudianteYaEsMiembroException(
                            "El estudiante ya es miembro del parche");
                }

                var invitacionExistente = invitacionRepository
                        .findByParcheIdAndEstudianteInvitadoId(parcheId, estudianteId);
                if (invitacionExistente.isPresent() &&
                        invitacionExistente.get().getEstado() == EstadoInvitacion.PENDIENTE) {
                    throw new InvitacionDuplicadaException(
                            "Ya existe una invitación pendiente para este estudiante");
                }

                Invitacion nuevaInvitacion = Invitacion.builder()
                        .parcheId(parcheId)
                        .capitanId(capitanId)
                        .estudianteInvitadoId(estudianteId)
                        .estado(EstadoInvitacion.PENDIENTE)
                        .build();

                Invitacion guardada = invitacionRepository.save(nuevaInvitacion);
                invitacionesCreadas.add(guardada);

                eventPublisher.publishEvent(new InvitacionEnviadaEvent(
                        guardada.getId(),
                        guardada.getParcheId(),
                        guardada.getEstudianteInvitadoId(),
                        guardada.getCapitanId()
                ));

            } catch (EstudianteYaEsMiembroException | InvitacionDuplicadaException e) {
                errores.add(ErrorResponse.builder()
                        .estudianteId(estudianteId)
                        .error(e.getMessage())
                        .status(409)
                        .build());
            } catch (Exception e) {
                errores.add(ErrorResponse.builder()
                        .estudianteId(estudianteId)
                        .error("Error interno: " + e.getMessage())
                        .status(400)
                        .build());
            }
        }

        return EnviarInvitacionResponse.builder()
                .invitacionesCreadas(invitacionesCreadas.stream()
                        .map(this::toResponse)
                        .toList())
                .errores(errores)
                .build();
    }

    private InvitacionResponse toResponse(Invitacion invitacion) {
        return InvitacionResponse.builder()
                .id(invitacion.getId())
                .parcheId(invitacion.getParcheId())
                .estudianteInvitadoId(invitacion.getEstudianteInvitadoId())
                .estado(invitacion.getEstado())
                .fechaEnvio(invitacion.getFechaEnvio())
                .build();
    }
}
