package com.kkori.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.config.GMSConfig;
import com.kkori.dto.QuestionForm;
import com.kkori.exception.interview.TailQuestionException;
import com.kkori.message.InterviewMessages;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TailQuestionGenerator {

    private final GMSConfig config;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DEFAULT_MODEL_NAME = "gpt-4o";

    public List<String> generateTailQuestions(Map<QuestionForm, String> questionAnswer) {
        try {
            String json = makeMessageJson(questionAnswer);
            RequestBody requestBody = RequestBody.create(
                    json, MediaType.parse("application/json")
            );
            Request request = buildRequest(requestBody);
            return executeGenerationRequest(request);
        } catch (Exception e) {
            throw TailQuestionException.tailQuestionGenerationFailed();
        }
    }


    private Request buildRequest(RequestBody requestBody) {
        return new Request.Builder()
                .url(config.getGptUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .post(requestBody)
                .build();
    }

    private List<String> executeGenerationRequest(Request request) throws Exception {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw TailQuestionException.tailQuestionGenerationFailed();
            }
            return parseTailQuestionResponse(response.body().string());
        }
    }

    private List<String> parseTailQuestionResponse(String responseBody) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String responseMessage = jsonNode.get("choices").get(0).get("message").get("content").asText();

        List<String> result = new ArrayList<>();

        for (String line : responseMessage.split("\n")) {
            String parsed = line.trim();
            if (!parsed.isEmpty()) {
                result.add(parsed);
            }
        }

        return result;
    }

    private String makeMessageJson(Map<QuestionForm, String> questionAnswer) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "developer", "content", InterviewMessages.TAIL_QUESTION_SYSTEM_MESSAGE));
        questionAnswer.forEach((question, answer) -> {
            messages.add(Map.of("role", "assistant", "content", question.getQuestionText()));
            messages.add(Map.of("role", "user", "content", answer));
        });
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("model", DEFAULT_MODEL_NAME);
        jsonMap.put("messages", messages);

        try {
            String json = objectMapper.writeValueAsString(jsonMap);
            return json;
        } catch (JsonProcessingException e) {
            throw TailQuestionException.jsonConversionFailed();
        }
    }
}
