package com.example.web.services;

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
import java.util.function.Predicate;

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

    public void delete(long id)
    {
        patientRepository.delete(patientRepository.findById(id).get());
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

        patient.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return patientRepository.save(patient);
    }

    private static Predicate<Patient> buildPatientFilter(String fullName, Integer maxAge, Integer ageGreaterThan, String gender)
    {
        Predicate<Patient> isAgeGreater = patient -> ageGreaterThan == null
                                                     || patient.getAge() > ageGreaterThan;

        Predicate<Patient> isAgeLess = patient -> maxAge == null
                                                  || patient.getAge() < maxAge;

        Predicate<Patient> isFullNameContains = patient -> fullName == null
                                                           || fullName.isBlank()
                                                           || patient.getFullName().contains(fullName);

        Predicate<Patient> isGenderCorrect = patient -> gender == null
                                                        || gender.isBlank()
                                                        || gender.equalsIgnoreCase(patient.getGender());

        return isAgeGreater.and(isAgeLess).and(isFullNameContains).and(isGenderCorrect);
    }

    public Page<Patient> getWithSpecification(String fullName, Integer maxAge, Integer minAge, String gender, Pageable paging)
    {
        return getAll(ageGreaterThan(minAge)
                              .and(ageLessThan(maxAge))
                              .and(fullNameContains(fullName))
                              .and(PatientSpecifications.genderCorrect(gender)), paging);
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

    public Page<Patient> findAll(Pageable paging)
    {
        return patientRepository.findAll(paging);
    }
}