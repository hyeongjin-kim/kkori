package com.kkori.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkori.config.GMSConfig;
import java.io.File;
import java.io.IOException;
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

    private final GMSConfig config;

    public String transcribe(String filePath) {
        OkHttpClient client = new OkHttpClient();

        File audioFile = new File(filePath);

        RequestBody fileBody = RequestBody.create(audioFile, MediaType.parse("audio/m4a"));

        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(), fileBody)
                .addFormDataPart("model", "whisper-1")
                .build();

        Request request = new Request.Builder()
                .url(config.getWhisperUrl())
                .addHeader("Authorization",
                        "Bearer " + config.getApiKey())
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String transcript = jsonNode.get("text").asText();
                System.out.println(transcript);
                return transcript;
            } else {
                String failMessage = "Request failed: " + response.code() + " " + response.message();
                System.err.println(failMessage);
                return failMessage;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
