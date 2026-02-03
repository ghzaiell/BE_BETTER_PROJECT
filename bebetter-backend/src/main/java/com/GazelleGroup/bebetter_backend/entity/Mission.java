package com.GazelleGroup.bebetter_backend.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class Mission {
    @Id
    private String id = new ObjectId().toString();
    private String name;
    private Boolean etat;
    private String description; // ✅ new field

    public Mission(String id, String name, Boolean etat, String description) {
        this.id = id;
        this.name = name;
        this.etat = false;
        this.description = description;
    }

    public Mission() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEtat() {
        return etat;
    }

    public void setEtat(Boolean etat) {
        this.etat = etat;
    }

    public String getDescription() {   // ✅ getter
        return description;
    }

    public void setDescription(String description) {  // ✅ setter
        this.description = description;
    }
}
