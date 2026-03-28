package com.example.myproject.Controller;

import com.example.myproject.Model.User;
import com.example.myproject.Repostiory.UserRepository;
import com.example.myproject.dto.*;
import com.example.myproject.Security.JwtUtil;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Register User
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDTO request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepo.save(user);

        return "User registered successfully";
    }

    // Login
    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(token);
    }
}