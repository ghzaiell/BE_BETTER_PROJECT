package com.GazelleGroup.bebetter_backend.service;

import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface IUserService {
    Utilisateur registerUser(Utilisateur utilisateur);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    Utilisateur getUserById(String id);
    Utilisateur updateUser(String id, Utilisateur utilisateur);
    void deleteUser(String id);
    List<Utilisateur> getAllUsers();
}
