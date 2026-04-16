package com.marjane.ems.Entities;

import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "technicians")
@Data
@EqualsAndHashCode(callSuper = true)
public class Technician extends User {
    @PrePersist
    public void generateEID() {
        if (this.getEID() == null) {
            this.setEID("T" + System.currentTimeMillis());
        }
    }
    @OneToMany(mappedBy = "technician")
    private List<Ticket> tickets;
}
