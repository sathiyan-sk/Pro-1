package com.trackerpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticContentController {
    
    /**
     * Root redirect to index page
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }
    
    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "loginPage.html";
    }
    
    /**
     * Registration page
     */
    @GetMapping("/register")
    public String register() {
        return "registerPage.html";
    }
    
    /**
     * Admin dashboard
     */
    @GetMapping("/admin")
    public String admin() {
        return "adminPage.html";
    }
    
    /**
     * Forgot password page
     */
    @GetMapping("/forgot")
    public String forgot() {
        return "forget.html";
    }
}