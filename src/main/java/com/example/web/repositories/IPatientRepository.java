package com.example.web.repositories;

import com.example.web.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface IPatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient>
{
    // Некорректная работа
    Patient findTopByOrderByUpdatedAtDesc();
    // Необходима дополнительная проверка на null
    Patient findTopByUpdatedAtIsNotNullOrderByUpdatedAtDesc();
}
