package com.GazelleGroup.bebetter_backend.controller;

import com.GazelleGroup.bebetter_backend.entity.Mission;
import com.GazelleGroup.bebetter_backend.entity.Subject;
import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.service.SubjectService;
import com.GazelleGroup.bebetter_backend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/subject")
public class SubjectController {



    private SubjectService subjectService;

    @PostMapping("/newSubjects/{username}")
    public ResponseEntity<Utilisateur> addSubject(
            @PathVariable String username,
            @RequestBody String prompt) {
        try {
            Utilisateur user = subjectService.createSubject(prompt, username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/deleteSubject/{username}/{subjectId}")
    public ResponseEntity<String> deleteSubject(
            @PathVariable String username,
            @PathVariable String subjectId) {
        try {
            subjectService.deleteSubject(username,subjectId);
            return ResponseEntity.ok("subject deleted ");
        }  catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User or subject not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }

    }

    @PutMapping("done/{username}/{subjectId}/{missionId}")
    public ResponseEntity<String> updateDoneMission(
            @PathVariable String username ,
            @PathVariable String subjectId ,
            @PathVariable String missionId
            ) {
        try {
            subjectService.changeEtatMission(username, subjectId, missionId);
            return ResponseEntity.ok("subject updated");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User or subject not found");
        } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body("An error occurred: " + e.getMessage());
        }
    }


    // ✅ 4️⃣ Delete one mission from a subject
    @DeleteMapping("/deleteMission/{username}/{subjectId}/{missionId}")
    public ResponseEntity<String> deleteMission(
            @PathVariable String username,
            @PathVariable String subjectId,
            @PathVariable String missionId) {
        try {
            subjectService.deleteMission(username, subjectId, missionId);
            return ResponseEntity.ok("Mission deleted");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User or mission not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    // ✅ 5️⃣ Get one subject by ID
    @GetMapping("/getSubject/{username}/{subjectId}")
    public ResponseEntity<Subject> getSubject(
            @PathVariable String username,
            @PathVariable String subjectId) {
        try {
            Subject subject = subjectService.getSubject(username, subjectId);
            if (subject == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            return ResponseEntity.ok(subject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ 6️⃣ Get all subjects of a user
    @GetMapping("/getAllSubjects/{username}")
    public ResponseEntity<List<Subject>> getAllSubjects(@PathVariable String username) {
        try {
            List<Subject> subjects = subjectService.getAllSubjects(username);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // ✅ 7️⃣ Get all missions of one subject
    @GetMapping("/getMissions/{username}/{subjectId}")
    public ResponseEntity<List<Mission>> getMissions(
            @PathVariable String username,
            @PathVariable String subjectId) {
        try {
            List<Mission> missions = subjectService.getMissions(username, subjectId);
            return ResponseEntity.ok(missions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}