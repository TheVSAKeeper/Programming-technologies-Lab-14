package com.example.web.services;

import com.example.web.dto.PatientFilter;
import com.example.web.entities.Patient;
import com.example.web.repositories.IPatientRepository;
import com.example.web.repositories.specifications.PatientSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.example.web.repositories.specifications.PatientSpecifications.*;

@Service
public class PatientsService
{
    private IPatientRepository patientRepository;

    @Autowired
    public void setPatientRepository(IPatientRepository patientRepository)
    {
        this.patientRepository = patientRepository;
    }

    public Page<Patient> getAll(Pageable paging)
    {
        return patientRepository.findAll(paging);
    }

    public Page<Patient> getAll(Specification<Patient> specification, Pageable paging)
    {
        return patientRepository.findAll(specification, paging);
    }

    public Patient getById(Long id)
    {
        return patientRepository.findById(id).orElse(null);
    }

    public Page<Patient> getWithSpecification(PatientFilter filter, Pageable paging)
    {
        return getAll(ageGreaterThan(filter.minAge())
                              .and(ageLessThan(filter.maxAge()))
                              .and(fullNameContains(filter.fullName()))
                              .and(PatientSpecifications.genderCorrect(filter.gender())), paging);
    }

    public void delete(long id)
    {
        Optional<Patient> patient = patientRepository.findById(id);

        patient.ifPresent(value -> patientRepository.delete(value));
    }

    public Patient update(Patient patient)
    {
        if (patient.getFirstName().isBlank()
            || patient.getLastName().isBlank()
            || patient.getBirthDate().after(java.sql.Date.valueOf(LocalDate.now()))
            || patient.getGender().isBlank()
            || patient.getAge() < 0)
        {
            return null;
        }

        // Если разделять создание и изменение.
        // При создании id отсутствует.
        // if (patient.getPatientId() != null)
        // {
        //      patient.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        // }

        patient.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        return patientRepository.save(patient);
    }

    public Integer getYoungestPatient()
    {
        return patientRepository.findAll()
                                .stream()
                                .mapToInt(Patient::getAge)
                                .min()
                                .orElse(0);
    }

    public Integer getOldestPatient()
    {
        return patientRepository.findAll()
                                .stream()
                                .mapToInt(Patient::getAge)
                                .max()
                                .orElse(0);
    }

    public Patient getLastUpdated()
    {
        // return patientRepository.findTopByOrderByUpdatedAtDesc(); // -

        return patientRepository.findTopByUpdatedAtIsNotNullOrderByUpdatedAtDesc(); // +

        // return patientRepository.findAll()
        // .stream()
        // .filter(patient -> patient.getUpdatedAt()!=null)
        // .sorted((o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()))
        // .findFirst()
        // .orElse(null); // +
    }
}

