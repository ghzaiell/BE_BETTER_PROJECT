package com.GazelleGroup.bebetter_backend.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Subject {
@Id
private String id = new ObjectId().toString();
private String name;
private String description;
private List<Mission> missions ;

    public Subject(String id, String description, String name, List<Mission> missions) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.missions = missions;
    }

    public Subject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public int totalMissions (){
        return (int) this.getMissions().stream().count();
    }
    public void addMission(Mission m) {
        if (this.missions == null) {
            this.missions = new ArrayList<>();
        }
        this.missions.add(m);
    }


}
