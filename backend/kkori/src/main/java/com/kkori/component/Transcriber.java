package com.kkori.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.config.GMSConfig;
import com.kkori.exception.audio.AudioProcessingException;
import java.io.File;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Transcriber {

    private static final MediaType AUDIO_M4A = MediaType.parse("audio/m4a");
    private static final String WHISPER_MODEL = "whisper-1";

    private final GMSConfig config;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String transcribe(String filePath) {
        try {
            MultipartBody requestBody = buildRequestBody(filePath);
            Request request = buildRequest(requestBody);
            return executeTranscriptionRequest(request);
        } catch (Exception e) {
            throw AudioProcessingException.audioTranscriptionFailed();
        }
    }

    private MultipartBody buildRequestBody(String filePath) {
        File audioFile = new File(filePath);
        RequestBody fileBody = RequestBody.create(audioFile, AUDIO_M4A);

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(), fileBody)
                .addFormDataPart("model", WHISPER_MODEL)
                .build();
    }

    private Request buildRequest(MultipartBody requestBody) {
        return new Request.Builder()
                .url(config.getWhisperUrl())
                .addHeader("Authorization", "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();
    }

    private String executeTranscriptionRequest(Request request) throws Exception {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw AudioProcessingException.apiCallFailed();
            }

            return parseTranscriptionResponse(response.body().string());
        }
    }

    private String parseTranscriptionResponse(String responseBody) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("text").asText();
    }
}