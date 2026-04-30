package com.charizard.compiled.hangout_service.application.usecase;

import com.charizard.compiled.hangout_service.application.dto.response.InvitacionResponse;
import com.charizard.compiled.hangout_service.domain.events.InvitacionAceptadaEvent;
import com.charizard.compiled.hangout_service.domain.exceptions.InvitacionYaRespondidaException;
import com.charizard.compiled.hangout_service.domain.exceptions.LimiteParchesAlcanzadoException;
import com.charizard.compiled.hangout_service.domain.model.Invitacion;
import com.charizard.compiled.hangout_service.domain.model.Member;
import com.charizard.compiled.hangout_service.domain.model.Parche;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import com.charizard.compiled.hangout_service.domain.model.enums.MemberRole;
import com.charizard.compiled.hangout_service.domain.ports.in.ResponderInvitacionInputPort;
import com.charizard.compiled.hangout_service.domain.ports.out.InvitacionRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.MemberRepositoryPort;
import com.charizard.compiled.hangout_service.domain.ports.out.ParcheRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponderInvitacionUseCase implements ResponderInvitacionInputPort {

    private final InvitacionRepositoryPort invitacionRepository;
    private final ParcheRepositoryPort parcheRepository;
    private final MemberRepositoryPort memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public InvitacionResponse responderInvitacion(UUID invitacionId, UUID estudianteId, EstadoInvitacion respuesta) {
        if (respuesta == EstadoInvitacion.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de respuesta no puede ser PENDIENTE");
        }

        Invitacion invitacion = invitacionRepository.findById(invitacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitación no encontrada"));

        if (!invitacion.getEstudianteInvitadoId().equals(estudianteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No eres el invitado de esta invitación");
        }

        if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
            throw new InvitacionYaRespondidaException();
        }

        if (respuesta == EstadoInvitacion.ACEPTADA) {
            Parche parche = parcheRepository.findById(invitacion.getParcheId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parche no encontrado"));

            int miembrosActuales = memberRepository.countByParcheId(invitacion.getParcheId());
            if (miembrosActuales >= parche.getMaximumQuota()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El parche ya no tiene cupo disponible");
            }

            if (memberRepository.countParchesActivosByStudentId(estudianteId) >= 5) {
                throw new LimiteParchesAlcanzadoException();
            }

            Member nuevoMiembro = Member.builder()
                    .parcheId(invitacion.getParcheId())
                    .studentId(estudianteId)
                    .memberRole(MemberRole.STUDENT)
                    .build();
            memberRepository.save(nuevoMiembro);

            eventPublisher.publishEvent(new InvitacionAceptadaEvent(
                    invitacionId, invitacion.getParcheId(), estudianteId, invitacion.getCapitanId()));
        }

        invitacion.setEstado(respuesta);
        invitacion.setFechaRespuesta(LocalDateTime.now());
        Invitacion actualizada = invitacionRepository.save(invitacion);

        return toResponse(actualizada);
    }

    private InvitacionResponse toResponse(Invitacion invitacion) {
        return InvitacionResponse.builder()
                .id(invitacion.getId())
                .parcheId(invitacion.getParcheId())
                .estudianteInvitadoId(invitacion.getEstudianteInvitadoId())
                .estado(invitacion.getEstado())
                .fechaEnvio(invitacion.getFechaEnvio())
                .fechaRespuesta(invitacion.getFechaRespuesta())
                .build();
    }
}
