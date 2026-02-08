package com.GazelleGroup.bebetter_backend.service;

import com.GazelleGroup.bebetter_backend.entity.Mission;
import com.GazelleGroup.bebetter_backend.entity.Subject;
import com.GazelleGroup.bebetter_backend.entity.Utilisateur;
import com.GazelleGroup.bebetter_backend.repository.SubjectRepository;
import com.GazelleGroup.bebetter_backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
@Service
public class SubjectService implements ISubjectService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;


    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    public SubjectService(UserRepository userRepository, SubjectRepository subjectRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
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




    public Utilisateur createSubject(String prompt, String username) throws JsonProcessingException {
        String geminiUrl = geminiApiUrl + "?key=" + geminiApiKey;;

        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String predefined = """
You are a professional soft skills coach.
Always answer ONLY in JSON format without any explanation or extra text.
The JSON structure must match exactly this format:

{
  "name": "clear subject name",
  "description": "short description of the problem including:
    - what the problem is
    - how often it usually happens (frequency)
    - the main reason (root cause)",
  "missions": [
    {
      "name": "mission name",
      "description": "how to do this mission and why it is good",
      "etat": "false"
    }
  ]
}

Rules:
- Write concise and professional text.
- Provide 5 to 10 missions depending on the problem.
- Do not return anything outside the JSON object.
- etat de mission always false

Now the user problem is:
""";

        String fullPrompt = predefined + prompt;

        // ✅ Escape prompt properly
        String safePrompt = mapper.writeValueAsString(fullPrompt);

        // ✅ Build proper JSON body
        String requestBody = "{ \"contents\": [{ \"parts\":[{\"text\":" + safePrompt + "}]}]}";

        String response;
        try {
            response = restTemplate.postForObject(geminiUrl, requestBody, String.class);
            System.out.println("===== RAW GEMINI RESPONSE =====");
            System.out.println(response);
            System.out.println("===== END RAW RESPONSE =====");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("Gemini API error: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            throw e;
        }

        // ✅ Extract generated text
        JsonNode rootNode = mapper.readTree(response);
        String textResponse = rootNode.at("/candidates/0/content/parts/0/text").asText();

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
