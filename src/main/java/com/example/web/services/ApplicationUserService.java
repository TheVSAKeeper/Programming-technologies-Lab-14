package com.example.web.services;

import com.example.web.entities.ApplicationRole;
import com.example.web.entities.ApplicationUser;
import com.example.web.repositories.IApplicationUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApplicationUserService implements UserDetailsService
{
    private IApplicationUserRepository userRepository;
    private ApplicationRoleService roleService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(IApplicationUserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleService(ApplicationRoleService roleService)
    {
        this.roleService = roleService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<ApplicationUser> findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        ApplicationUser user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));

        return new User(user.getUsername(),
                        user.getPassword(),
                        user.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toList())
        );
    }

    public ApplicationUser createNewUser(String username, String rawPassword, String firstName, String lastName, String patronymic, ApplicationRole... roles)
    {
        ApplicationUser user = new ApplicationUser(username,
                                                   passwordEncoder.encode(rawPassword),
                                                   firstName,
                                                   lastName,
                                                   patronymic,
                                                   List.of(roles));

        return userRepository.save(user);
    }

    @PostConstruct
    private void initDefaultUsers()
    {
        if (userRepository.count() == 0)
        {
            createNewUser("admin", "pass1", "Иванов", "Алексей", "Петрович",
                          roleService.getAdminRole(), roleService.getDoctorRole(), roleService.getNurseRole());
            createNewUser("doctor", "pass2", "Козлова", "Ирина", "Викторовна",
                          roleService.getDoctorRole(), roleService.getNurseRole());
            createNewUser("nurse", "pass3", "Смирнова", "Людмила", "Игоревна",
                          roleService.getNurseRole());
        }
    }
}