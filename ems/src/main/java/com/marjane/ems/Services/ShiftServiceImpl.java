package com.marjane.ems.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.marjane.ems.DAL.ShiftRepository;
import com.marjane.ems.Entities.Shift;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public Shift createShift(Shift shift) {
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }
        return shiftRepository.save(shift);
    }

    @Override
    public Optional<Shift> getShiftById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid shift ID");
        }
        return shiftRepository.findById(id);
    }

    @Override
    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    @Override
    public List<Shift> getShiftsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        List<Shift> shifts = shiftRepository.findByDateShift(date);

        if (shifts.isEmpty()) {
            throw new RuntimeException("No shifts found for date: " + date);
        }

        return shifts;
    }

    @Override
    public List<Shift> getShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        List<Shift> shifts = shiftRepository.findByDateShiftBetween(startDate, endDate);

        if (shifts.isEmpty()) {
            throw new RuntimeException("No shifts found in the date range");
        }

        return shifts;
    }

    @Override
    public List<Shift> getShiftsByUser(Long userId) {
        // Note: This requires a query that joins through the many-to-many relationship
        // Implement based on your specific entity mapping
        throw new UnsupportedOperationException("getShiftsByUser not yet implemented - requires many-to-many query");
    }

    @Override
    public Shift updateShift(Long id, Shift updatedShift) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid shift ID");
        }

        if (updatedShift == null) {
            throw new IllegalArgumentException("Shift cannot be null");
        }

        Shift shift = shiftRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shift not found with ID: " + id));

        shift.setDateShift(updatedShift.getDateShift());
        shift.setStarTime(updatedShift.getStarTime());
        shift.setEndTime(updatedShift.getEndTime());

        return shiftRepository.save(shift);
    }

    @Override
    public Shift assignUserToShift(Long shiftId, Long userId) {
        // Note: This requires implementation based on your many-to-many relationship
        throw new UnsupportedOperationException("assignUserToShift not yet implemented");
    }

    @Override
    public Shift removeUserFromShift(Long shiftId, Long userId) {
        // Note: This requires implementation based on your many-to-many relationship
        throw new UnsupportedOperationException("removeUserFromShift not yet implemented");
    }

    @Override
    public void deleteShift(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid shift ID");
        }

        if (!shiftRepository.existsById(id)) {
            throw new RuntimeException("Shift not found with ID: " + id);
        }

        shiftRepository.deleteById(id);
    }
}
