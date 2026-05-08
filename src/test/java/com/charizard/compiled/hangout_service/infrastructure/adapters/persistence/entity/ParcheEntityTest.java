package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheCategory;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
                .category(ParcheCategory.CINEMA)
                .type(ParcheType.PUBLIC)
                .maximumQuota(10)
                .date(LocalDate.now().plusDays(1))
                .hour(LocalTime.of(15, 0))
                .status(ParcheStatus.ACTIVE)
                .captainId(UUID.randomUUID())
                .build();
    }

    @Test
    void shouldFailWhenMaximumQuotaIsLessThanTwo() {
        ParcheEntity parche = buildValidParche();
        parche.setMaximumQuota(1);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertFalse(violations.isEmpty(), "Debe haber al menos una violación");
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("maximumQuota")),
                "La violación debe ser en el campo maximumQuota"
        );
    }

    @Test
    void shouldFailWhenNameIsNull() {
        ParcheEntity parche = buildValidParche();
        parche.setName(null);

        Set<ConstraintViolation<ParcheEntity>> violations = validator.validate(parche);

        assertFalse(violations.isEmpty(), "Debe haber al menos una violación");
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")),
                "La violación debe ser en el campo name"
        );
    }
}