package com.marjane.ems.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "administrators")
@Data
@EqualsAndHashCode(callSuper = true)
public class Administrator extends User {
    @PrePersist
    public void generateEID() {
        if (this.getEID() == null) {
            this.setEID("A" + System.currentTimeMillis());
        }
    }
}
