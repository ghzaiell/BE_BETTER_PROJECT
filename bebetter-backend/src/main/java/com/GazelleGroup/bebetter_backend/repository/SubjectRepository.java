package com.GazelleGroup.bebetter_backend.repository;

import com.GazelleGroup.bebetter_backend.entity.Subject;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    @Override
    Optional<Subject> findById(String id);
}
