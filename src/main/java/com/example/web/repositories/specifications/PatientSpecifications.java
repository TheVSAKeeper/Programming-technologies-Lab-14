package com.example.web.repositories.specifications;

import com.example.web.entities.Patient;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDate;

public class PatientSpecifications
{
    public static Specification<Patient> fullNameContains(String fullName)
    {
        return (root, query, builder) ->
        {
            if (fullName == null)
            {
                return builder.isTrue(builder.literal(true));
            }

            String pattern = "%" + fullName.toLowerCase() + "%";

            Predicate firstNamePredicate = builder.like(builder.lower(root.get("firstName")), pattern);
            Predicate lastNamePredicate = builder.like(builder.lower(root.get("lastName")), pattern);
            Predicate patronymicPredicate = builder.like(builder.lower(root.get("patronymic")), pattern);

            return builder.or(firstNamePredicate, lastNamePredicate, patronymicPredicate);
        };
    }

    public static Specification<Patient> ageLessThan(Integer maxAge)
    {
        return (root, query, builder) ->
        {
            if (maxAge == null)
            {
                return builder.isTrue(builder.literal(true));
            }

            return builder.greaterThan(root.get("birthDate"), getBirthdateLimit(maxAge));
        };
    }

    public static Specification<Patient> ageGreaterThan(Integer minAge)
    {
        return (root, query, builder) ->
        {
            if (minAge == null)
            {
                return builder.isTrue(builder.literal(true));
            }

            return builder.lessThan(root.get("birthDate"), getBirthdateLimit(minAge));
        };
    }

    private static Timestamp getBirthdateLimit(Integer age)
    {
        return Timestamp.valueOf(LocalDate.now().minusYears(age).atStartOfDay());
    }

    public static Specification<Patient> genderCorrect(String gender)
    {
        return (root, query, builder) ->
        {
            if (gender == null)
            {
                return builder.isTrue(builder.literal(true));
            }

            return builder.equal(root.get("gender"), gender);
        };
    }
}
