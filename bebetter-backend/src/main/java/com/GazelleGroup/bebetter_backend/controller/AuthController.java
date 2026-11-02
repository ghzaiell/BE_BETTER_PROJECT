package com.GazelleGroup.bebetter_backend.controller;

import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.security.JwtUtil;
import com.GazelleGroup.bebetter_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Utilisateur utilisateur) {
        if (isBlank(utilisateur.getUsername()) || isBlank(utilisateur.getEmail())
                || isBlank(utilisateur.getPassword())) {
            return new ResponseEntity<>(Map.of("message", "Username, email and password are required"),
                    HttpStatus.BAD_REQUEST);
        }
        if (utilisateur.getPassword().length() < 6) {
            return new ResponseEntity<>(Map.of("message", "Password must be at least 6 characters"),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            Utilisateur saved = userService.registerUser(utilisateur);
            // Sign the new user in right away
            Map<String, String> map = new HashMap<>();
            map.put("token", jwtUtil.generateToken(saved.getUsername()));
            return new ResponseEntity<>(map, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        System.out.println("Received login for: " + username);
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("Authentication successful for: " + authentication.getName());

            UserDetails user = userService.loadUserByUsername(username);

            Map<String, String> map = new HashMap<>();
            map.put("token", jwtUtil.generateToken(user.getUsername()));
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}
