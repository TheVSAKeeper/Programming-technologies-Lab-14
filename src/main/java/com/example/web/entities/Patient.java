package com.example.web.entities;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;

@Entity
public class Patient
{
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long patientId;

    @Basic
    @Column(nullable = false, length = 20)
    private String lastName;

    @Basic
    @Column(nullable = false, length = 20)
    private String firstName;

    @Basic
    @Column(length = 20)
    private String patronymic;

    @Basic
    @Column(length = 1)
    private String gender;

    @Basic
    @Column(nullable = false)
    private Date birthDate;

    @Basic
    private Timestamp updatedAt;

    public Timestamp getUpdatedAt()
    {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Patient()
    {
    }

    public Patient(String lastName, String firstName, String patronymic, Date birthDate)
    {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public Long getPatientId()
    {
        return patientId;
    }

    public void setPatientId(Long patientId)
    {
        this.patientId = patientId;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getPatronymic()
    {
        return patronymic;
    }

    public void setPatronymic(String patronymic)
    {
        this.patronymic = patronymic;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    @Override
    public String toString()
    {
        return lastName + " " + firstName + " " + patronymic + " " + gender + " " + birthDate;
    }

    public String getFullName()
    {
        return lastName + " " + firstName + " " + patronymic;
    }

    public int getAge()
    {
        if (birthDate == null)
        {
            return -1;
        }

        LocalDate now = LocalDate.now();
        LocalDate birthDateLocal = birthDate.toLocalDate();
        return Period.between(birthDateLocal, now).getYears();
    }
}
