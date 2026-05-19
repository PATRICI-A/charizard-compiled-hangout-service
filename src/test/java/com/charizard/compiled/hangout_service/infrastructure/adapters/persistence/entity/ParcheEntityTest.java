package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ParcheEntityTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private ParcheEntity buildValidParche() {
        return ParcheEntity.builder()
                .name("Parche del barrio")
                .description("Un parche cualquiera")
                .place("Escuela")
                .category("CINEMA")
                .type(ParcheType.PUBLIC)
                .maximumQuota(10)
                .date(LocalDate.now().plusDays(1))
                .hour(LocalTime.of(15, 0))
                .status(ParcheStatus.ACTIVE)
                .ownerId(UUID.randomUUID())
                .build();
    }

    @Test
    @DisplayName("entidad válida no genera violaciones de constraints")
    void entidadValida_sinViolaciones() {
        ParcheEntity parche = buildValidParche();

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("maximumQuota igual a 1 es válido (mínimo reducido a 1)")
    void maximumQuota_igualUno_esValido() {
        ParcheEntity parche = buildValidParche();
        parche.setMaximumQuota(1);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("maximumQuota"));
    }

    @Test
    @DisplayName("maximumQuota igual a 0 genera violación de constraint")
    void maximumQuota_cero_violaConstraint() {
        ParcheEntity parche = buildValidParche();
        parche.setMaximumQuota(0);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maximumQuota"));
    }

    @Test
    @DisplayName("maximumQuota igual a 2 es válido")
    void maximumQuota_igualDos_esValido() {
        ParcheEntity parche = buildValidParche();
        parche.setMaximumQuota(2);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).noneMatch(v -> v.getPropertyPath().toString().equals("maximumQuota"));
    }

    @Test
    @DisplayName("maximumQuota mayor a 30 genera violación de constraint")
    void maximumQuota_mayorTreinta_violaConstraint() {
        ParcheEntity parche = buildValidParche();
        parche.setMaximumQuota(31);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("maximumQuota"));
    }

    @Test
    @DisplayName("name nulo genera violación de constraint")
    void name_nulo_violaConstraint() {
        ParcheEntity parche = buildValidParche();
        parche.setName(null);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
