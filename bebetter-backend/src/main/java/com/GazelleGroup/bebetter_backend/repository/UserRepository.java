package com.GazelleGroup.bebetter_backend.repository;

import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<Utilisateur, String> {

    Optional<Utilisateur>  findByUsername(String username);
}
