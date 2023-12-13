package com.example.web.dto;

// DTO для фильтрации для предотвращения разрастания параметров метода
// и упрощения добавления новых критериев поиска.
public record PatientFilter(String fullName, Integer maxAge, Integer minAge, String gender)
{
}