package com.example.web.configuration;

import com.example.web.services.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private ApplicationUserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(ApplicationUserService userService)
    {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }

/*    @Bean
    public UserDetailsService userDetailsService()
    {
        UserDetails admin = User.withUsername("admin")
                                .password(passwordEncoder.encode("pass1"))
                                .roles("NURSE", "DOCTOR", "ADMIN")
                                .build();
        UserDetails doctor = User.withUsername("doctor")
                                 .password(passwordEncoder.encode("pass2"))
                                 .roles("NURSE", "DOCTOR")
                                 .build();
        UserDetails nurse = User.withUsername("nurse")
                                .password(passwordEncoder.encode("pass3"))
                                .roles("NURSE")
                                .build();

        return new InMemoryUserDetailsManager(admin, doctor, nurse);
    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home")
                        .failureUrl("/home?error=true")
                        .permitAll()
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home?logout=true")
                        .deleteCookies("JSESSIONID")
                ).authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/home/**").permitAll()
                        .requestMatchers("/patients/**").hasAnyRole("NURSE", "DOCTOR", "ADMIN")
                        .requestMatchers("/patients/delete/**").hasAnyRole("ADMIN")
                        .requestMatchers("/login").anonymous()
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider()
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userService);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }
}