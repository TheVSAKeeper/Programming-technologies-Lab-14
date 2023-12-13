package com.example.web.controllers;

import com.example.web.services.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController
{
    private ApplicationUserService userService;

    @Autowired
    public void setUserService(ApplicationUserService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails)
    {
        if (userDetails != null)
        {
            model.addAttribute("user", userService.findByUsername(userDetails.getUsername()).get());
        }

        return "main/index";
    }
}
