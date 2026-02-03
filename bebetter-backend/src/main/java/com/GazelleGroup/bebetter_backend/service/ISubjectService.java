package com.GazelleGroup.bebetter_backend.service;

import com.GazelleGroup.bebetter_backend.entity.Mission;
import com.GazelleGroup.bebetter_backend.entity.Subject;
import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ISubjectService {

        void deleteSubject(String username, String subjectId);

        void changeEtatMission(String username, String subjectID, String missionId);

        Utilisateur createSubject(String prompt, String username) throws JsonProcessingException;

        void deleteMission(String username,String subjectId, String missionId);

        Subject getSubject(String username ,String subjectId);

        List<Subject> getAllSubjects(String username);

        List<Mission> getMissions(String username,String subjectId);



}
