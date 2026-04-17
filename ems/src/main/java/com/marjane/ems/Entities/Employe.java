package com.marjane.ems.Entities;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "employes")
@Data
@EqualsAndHashCode(callSuper = true)
public class Employe extends User {
    @PrePersist
    public void generateEID() {
        if (this.getEID() == null) {
            this.setEID("E" + System.currentTimeMillis());
        }
    }
    private String departement;
    private String activityStatus;
    
    @OneToMany(mappedBy = "creator")
    private List<Ticket> tickets;
    

}