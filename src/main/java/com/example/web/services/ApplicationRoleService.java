package com.example.web.services;

import com.example.web.entities.ApplicationRole;
import com.example.web.repositories.IApplicationRoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRoleService
{
    private IApplicationRoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(IApplicationRoleRepository roleRepository)
    {
        this.roleRepository = roleRepository;
    }

    public ApplicationRole getNurseRole()
    {
        return roleRepository.findByName("ROLE_NURSE").orElseThrow();
    }

    public ApplicationRole getDoctorRole()
    {
        return roleRepository.findByName("ROLE_DOCTOR").orElseThrow();
    }

    public ApplicationRole getAdminRole()
    {
        return roleRepository.findByName("ROLE_ADMIN").orElseThrow();
    }

    @PostConstruct
    public void init()
    {
        if (roleRepository.count() == 0)
        {
            roleRepository.save(new ApplicationRole("ROLE_NURSE"));
            roleRepository.save(new ApplicationRole("ROLE_DOCTOR"));
            roleRepository.save(new ApplicationRole("ROLE_ADMIN"));
        }
    }
}
