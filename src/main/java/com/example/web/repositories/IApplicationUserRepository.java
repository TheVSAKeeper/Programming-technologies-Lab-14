package com.example.web.repositories;

import com.example.web.entities.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IApplicationUserRepository extends JpaRepository<ApplicationUser, Long>
{
    Optional<ApplicationUser> findByUsername(String username);
}
