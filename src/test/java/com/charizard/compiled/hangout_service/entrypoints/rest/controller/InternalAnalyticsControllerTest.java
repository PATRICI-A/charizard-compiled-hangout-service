package com.charizard.compiled.hangout_service.entrypoints.rest.controller;

import com.charizard.compiled.hangout_service.domain.ports.in.JoinParcheInputPort;
import com.charizard.compiled.hangout_service.entrypoints.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InternalAnalyticsControllerTest {

    @Mock JoinParcheInputPort joinParcheService;

    @InjectMocks InternalAnalyticsController controller;

    MockMvc mockMvc;
    UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/parche-count retorna 200 con el conteo correcto")
    void getUserParcheCount_retorna200ConConteo() throws Exception {
        when(joinParcheService.getUserParcheCount(userId)).thenReturn(3);

        mockMvc.perform(get("/api/v1/parches/internal/user/{userId}/parche-count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("GET /internal/user/{userId}/parche-count retorna 0 cuando el usuario no tiene parches")
    void getUserParcheCount_retornaCeroSinParches() throws Exception {
        when(joinParcheService.getUserParcheCount(userId)).thenReturn(0);

        mockMvc.perform(get("/api/v1/parches/internal/user/{userId}/parche-count", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
