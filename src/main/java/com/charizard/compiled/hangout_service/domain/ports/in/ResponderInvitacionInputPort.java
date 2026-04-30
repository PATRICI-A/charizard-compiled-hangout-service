package com.charizard.compiled.hangout_service.domain.ports.in;

import com.charizard.compiled.hangout_service.application.dto.response.InvitacionResponse;
import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;

import java.util.UUID;

public interface ResponderInvitacionInputPort {

    InvitacionResponse responderInvitacion(UUID invitacionId, UUID estudianteId, EstadoInvitacion respuesta);
}
