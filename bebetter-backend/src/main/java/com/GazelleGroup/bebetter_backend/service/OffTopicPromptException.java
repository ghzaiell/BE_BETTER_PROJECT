package com.GazelleGroup.bebetter_backend.service;

// Thrown when the AI decides the user's prompt is not a soft-skills / personal-development problem
public class OffTopicPromptException extends RuntimeException {
    public OffTopicPromptException(String message) {
        super(message);
    }
}
