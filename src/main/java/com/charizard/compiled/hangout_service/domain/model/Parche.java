package com.charizard.compiled.hangout_service.domain.model;

import com.charizard.compiled.hangout_service.domain.model.enums.ParcheStatus;
import com.charizard.compiled.hangout_service.domain.model.enums.ParcheType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parche {

    private UUID id;
    private String name;
    private String description;
    private String place;
    private ParcheType type;
    private LocalDate date;
    private LocalTime hour;
    private int maximumQuota;
    private LocalDateTime dateRealization;
    private ParcheStatus status = ParcheStatus.ACTIVE;

    private UUID captainId;
    private LocalDateTime creationDate;
    private List<Member> members;

    private UUID eventId;

}
