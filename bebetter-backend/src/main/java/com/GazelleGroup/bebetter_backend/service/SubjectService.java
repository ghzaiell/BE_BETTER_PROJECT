package com.GazelleGroup.bebetter_backend.service;

import com.GazelleGroup.bebetter_backend.entity.Mission;
import com.GazelleGroup.bebetter_backend.entity.Subject;
import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.repository.SubjectRepository;
import com.GazelleGroup.bebetter_backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class SubjectService implements ISubjectService {

    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    @Value("${openrouter.api.key}")
    private String openRouterApiKey;

    @Value("${openrouter.model}")
    private String openRouterModel;


    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final String systemPrompt;

    public SubjectService(UserRepository userRepository, SubjectRepository subjectRepository,
                          @Value("${app.prompt.subject-coach}") Resource systemPromptFile) throws IOException {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.systemPrompt = systemPromptFile.getContentAsString(StandardCharsets.UTF_8);
    }

    public void deleteSubject(String username, String subjectId) {
        Optional<Utilisateur> u = userRepository.findByUsername(username);
        if (u.isPresent()) {
            u.get().getSubjects().removeIf(s -> s.getId().equals(subjectId));

        }
        userRepository.save(u.get());
    }


    public void changeEtatMission(String username, String subjectID, String missionId) {
        Optional<Utilisateur> user = userRepository.findByUsername(username);
        if (user.isPresent()) {

            user.get().getSubjects().stream().filter(s -> s.getId().equals(subjectID))
                    .findFirst().ifPresent(s -> s.getMissions().stream().filter(m -> m.getId().equals(missionId))
                            .findFirst().ifPresent(m -> m.setEtat(true)));
            userRepository.save(user.get());

        }

    }




    public static final int MAX_PROMPT_LENGTH = 4000;

    public Utilisateur createSubject(String prompt, String username) throws JsonProcessingException {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt is required");
        }
        if (prompt.length() > MAX_PROMPT_LENGTH) {
            throw new IllegalArgumentException("Prompt must be at most " + MAX_PROMPT_LENGTH + " characters");
        }

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        // ✅ OpenRouter (OpenAI-compatible) body: coach instructions as system message, user text as user message
        ObjectNode body = mapper.createObjectNode();
        body.put("model", openRouterModel);
        body.putObject("response_format").put("type", "json_object");
        ArrayNode messages = body.putArray("messages");
        messages.addObject().put("role", "system").put("content", systemPrompt);
        messages.addObject().put("role", "user").put("content", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openRouterApiKey);
        headers.set("X-Title", "BeBetter"); // optional, shows the app name in the OpenRouter dashboard

        String response;
        try {
            response = restTemplate.postForObject(openRouterApiUrl,
                    new HttpEntity<>(mapper.writeValueAsString(body), headers), String.class);
            System.out.println("===== RAW OPENROUTER RESPONSE =====");
            System.out.println(response);
            System.out.println("===== END RAW RESPONSE =====");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("OpenRouter API error: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        }

        // ✅ Extract generated text
        JsonNode rootNode = mapper.readTree(response);
        String textResponse = rootNode.at("/choices/0/message/content").asText();

        System.out.println("===== EXTRACTED TEXT =====");
        System.out.println(textResponse);
        System.out.println("===== END EXTRACTED TEXT =====");
        textResponse = textResponse
                .replaceAll("^```(json)?\\s*", "")
                .replaceAll("\\s*```$", "");
        System.out.println("===== new EXTRACTED TEXT =====");
        System.out.println(textResponse);
        System.out.println("===== END EXTRACTED TEXT =====");
        JsonNode subjectNode = mapper.readTree(textResponse);

        if (subjectNode.path("offTopic").asBoolean(false)) {
            throw new OffTopicPromptException(subjectNode.path("reason")
                    .asText("Your request is not about a soft skill or personal development problem."));
        }

        Subject s = new Subject();
        s.setName(subjectNode.path("name").asText("MISSING_NAME"));
        s.setDescription(subjectNode.path("description") .asText( "MISSING_DESCRIPTION"));

        JsonNode missionsNode = subjectNode.get("missions");
        if (missionsNode != null && missionsNode.isArray()) {
            for (JsonNode mission : missionsNode) {
                Mission m = new Mission();
                m.setName(mission.path("name").asText("MISSING_NAME"));
                m.setDescription(mission.path("description").asText("MISSING_DESCRIPTION"));
                m.setEtat(false);
                s.addMission(m);
            }
        }
        System.out.println("===== subject =====");
        System.out.println(s);
        System.out.println("===== subject =====");

        Utilisateur utilisateur = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        utilisateur.addSubject(s);
        userRepository.save(utilisateur);

        return utilisateur;
    }

    @Override
    public void deleteMission(String username,String subjectId, String missionId) {
        Optional<Utilisateur> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().getSubjects().stream()
                    .filter(s -> s.getId().equals(subjectId))
                    .findFirst()
                    .ifPresent(s -> {
                        boolean removed = s.getMissions()
                                .removeIf(m -> m.getId().equals(missionId));

                        // Save the user after modification
                        userRepository.save(user.get());
                    });
        }
    }


    @Override
    public Subject getSubject(String username ,String subjectId) {
        Optional<Utilisateur> utilisateur = userRepository.findByUsername(username);
        if (utilisateur.isPresent()) {
            return utilisateur.get().getSubjects().stream().
                    filter(s->s.getId().equals(subjectId)).findFirst().orElse(null);
        }
        else
        System.out.println("user not found");
        return null;
    }

    @Override
    public List<Subject> getAllSubjects(String username) {
        return userRepository.findByUsername(username)
                .map(Utilisateur::getSubjects)
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Mission> getMissions(String username, String subjectId) {
        Optional<Utilisateur> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            System.out.println("User not found");
            return Collections.emptyList();
        }

        Utilisateur user = optionalUser.get();
        return user.getSubjects().stream()
                .filter(s -> s.getId().equals(subjectId))
                .findFirst()
                .map(Subject::getMissions)
                .orElse(Collections.emptyList());
    }


}
