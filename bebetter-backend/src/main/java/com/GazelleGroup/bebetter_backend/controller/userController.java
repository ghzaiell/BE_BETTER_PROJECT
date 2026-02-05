package com.GazelleGroup.bebetter_backend.controller;

import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.repository.UserRepository;
import com.GazelleGroup.bebetter_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200")
public class userController {

    UserService userService;

    @GetMapping("/byId/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        Utilisateur utilisateur = userService.getUserById(userId);
        if(utilisateur == null){
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        return ResponseEntity.ok(utilisateur);
    }
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Utilisateur utilisateur = userService.getUserByUsername(username);
        if(utilisateur == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        return ResponseEntity.ok(utilisateur);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Utilisateur>> getAllUsers() {
        List<Utilisateur> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // HTTP 204
        }

        return ResponseEntity.ok(users); // HTTP 200 + JSON list
    }


    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
       if (userService.getUserById(userId) == null) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
       }
       userService.deleteUser(userId);
       return ResponseEntity.ok("User deleted");
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateUser(
            @PathVariable String userId,
            @RequestBody Utilisateur utilisateur) {
        if (userService.getUserById(userId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        userService.updateUser(userId, utilisateur);
        return ResponseEntity.ok("User updated");
    }

}
