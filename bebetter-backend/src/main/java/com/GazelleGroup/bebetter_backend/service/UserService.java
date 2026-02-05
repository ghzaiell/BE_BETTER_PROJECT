package com.GazelleGroup.bebetter_backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import com.GazelleGroup.bebetter_backend.entity.Mission;
import com.GazelleGroup.bebetter_backend.entity.Subject;
import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements IUserService , UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilisateur registerUser(Utilisateur utilisateur) {
        if (userRepository.findByUsername(utilisateur.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        } else {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
            userRepository.save(utilisateur);
            return utilisateur;
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        return new User(utilisateur.getUsername(),
                utilisateur.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(utilisateur.getRole())));
    }

    @Override
    public Utilisateur getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public Utilisateur updateUser(String id, Utilisateur utilisateur) {
        Utilisateur user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if(utilisateur.getUsername() != null && !utilisateur.getUsername().isEmpty()){
                user.setUsername(utilisateur.getUsername());}
            if(utilisateur.getPassword() != null && !utilisateur.getPassword().isEmpty()){
                user.setPassword(passwordEncoder.encode(utilisateur.getPassword()));}
            if(utilisateur.getEmail() != null && !utilisateur.getEmail().isEmpty()){
                user.setEmail(utilisateur.getEmail());}

            userRepository.save(user);
            return user;
        }
        else
            throw  new RuntimeException("User not found");

    }

    @Override
    public void deleteUser(String id)
    {
    userRepository.deleteById(id);
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }


    public Utilisateur getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
    }



}
