package com.example.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController
{
    @GetMapping("/login")
    public String login()
    {
        return "redirect:/home?denied=true";
    }

    @GetMapping("/logout")
    public String logout()
    {
        return "redirect:/home";
    }
}