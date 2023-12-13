package com.example.web.repositories;

import com.example.web.entities.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IApplicationRoleRepository extends JpaRepository<ApplicationRole, Long>
{
    Optional<ApplicationRole> findByName(String name);
}
