package com.example.web.controllers;

import com.example.web.entities.Patient;
import com.example.web.dto.PatientFilter;
import com.example.web.services.PatientsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/patients")
public class PatientsController
{
    private PatientsService patientsService;

    @Autowired
    public void setPatientsService(PatientsService patientsService)
    {
        this.patientsService = patientsService;
    }

    @GetMapping()
    public String index(Model model,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "maxAge", required = false) Integer maxAge,
            @RequestParam(name = "minAge", required = false) Integer minAge,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber)
    {
        Patient lastUpdated = patientsService.getLastUpdated();

        Page<Patient> patients;

        if (pageNumber == null || pageNumber <= 0)
        {
            pageNumber = 1;
        }

        Pageable paging = PageRequest.of(pageNumber - 1, 5);

        if (fullName != null)
        {
            if (gender != null && gender.isBlank())
            {
                gender = null;
            }

            PatientFilter filter = new PatientFilter(fullName, maxAge, minAge, gender);
            patients = patientsService.getWithSpecification(filter, paging);

            model.addAttribute("fullName", fullName);
            model.addAttribute("maxAge", maxAge);
            model.addAttribute("minAge", minAge);
            model.addAttribute("gender", gender);
        }
        else
        {
            patients = patientsService.getAll(paging);
        }

        model.addAttribute("lastUpdated", lastUpdated);
        model.addAttribute("totalPages", patients.getTotalPages());
        model.addAttribute("totalItems", patients.getTotalElements());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("maxPatientAge", patientsService.getOldestPatient());
        model.addAttribute("minPatientAge", patientsService.getYoungestPatient());
        model.addAttribute("patients", patients);

        return "patients/index";
    }

    @GetMapping("/reset")
    public String resetFilters()
    {
        return "redirect:/patients";
    }

    @GetMapping("/add")
    public String editPatient(Model model,
            @RequestParam(name = "patientId", required = false) Long patientId,
            HttpServletRequest request)
    {
        Patient patient;

        if (patientId == null)
        {
            patient = new Patient();
            patient.setBirthDate(java.sql.Date.valueOf(LocalDate.now().plusDays(1)));
        }
        else
        {
            patient = patientsService.getById(patientId);
        }

        model.addAttribute("patient", patient);
        model.addAttribute("isShowId", request.isUserInRole("ADMIN"));

        return "patients/edit";
    }

    @PostMapping("/add")
    public String savePatient(@ModelAttribute("patient") Patient patient,
            @RequestParam(name = "isNewPatient", required = false) Boolean isNewPatient)
    {
        if (isNewPatient != null && isNewPatient)
        {
            patient.setPatientId(null);
        }

        if (patientsService.update(patient) == null)
        {
            return "redirect:/patients?error=true";
        }

        return "redirect:/patients";
    }

    @PostMapping("/delete/{id}")
    public String deletePatientById(@ModelAttribute("patient") Patient patient, @PathVariable("id") long id)
    {
        patientsService.delete(id);
        return "redirect:/patients";
    }
}
