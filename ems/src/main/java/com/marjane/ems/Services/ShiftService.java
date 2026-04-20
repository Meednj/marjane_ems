package com.marjane.ems.Services;

import com.marjane.ems.Entities.Shift;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftService {
    Shift createShift(Shift shift);
    Optional<Shift> getShiftById(Long id);
    List<Shift> getAllShifts();
    List<Shift> getShiftsByDate(LocalDate date);
    List<Shift> getShiftsByDateRange(LocalDate startDate, LocalDate endDate);
    List<Shift> getShiftsByUser(Long userId);
    Shift updateShift(Long id, Shift shift);
    Shift assignUserToShift(Long shiftId, Long userId);
    Shift removeUserFromShift(Long shiftId, Long userId);
    void deleteShift(Long id);
}
