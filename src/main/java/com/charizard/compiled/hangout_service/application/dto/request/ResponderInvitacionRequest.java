package com.charizard.compiled.hangout_service.application.dto.request;

import com.charizard.compiled.hangout_service.domain.model.enums.EstadoInvitacion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResponderInvitacionRequest {

    @NotNull
    private EstadoInvitacion respuesta;
}
