package com.marjane.ems.DAL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.marjane.ems.Entities.Administrator;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
}
