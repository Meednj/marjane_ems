package com.marjane.ems.DAL;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marjane.ems.Entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    public Department findByName(String name);

    public boolean existsByName(String name);

}
