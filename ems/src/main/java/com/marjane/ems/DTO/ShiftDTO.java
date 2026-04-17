package com.marjane.ems.DTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ShiftDTO(
    Long id,
    LocalDate dateShift,
    LocalTime starTime,
    LocalTime endTime,
    List<Long> userIds
) {}
