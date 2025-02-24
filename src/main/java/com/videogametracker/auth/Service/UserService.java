package com.videogametracker.auth.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.BaseResponse;
import com.videogametracker.auth.Model.response.RegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, CompletableFuture<RegisterResponse>> pendingRequests = new ConcurrentHashMap<>();

    public ResponseEntity<BaseResponse> registerUser(RegisterRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            CompletableFuture<RegisterResponse> futureResponse = new CompletableFuture<>();
            request.setCorrelationId(UUID.randomUUID().toString());

            // give id for kafka
            pendingRequests.put(request.getCorrelationId(), futureResponse);
            var reqString = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("register-request-topic", reqString);

            result.setData(futureResponse.get(5, TimeUnit.SECONDS));
            result.setMessage("Registration success");
            result.setStatus(HttpStatus.OK.value());
            return ResponseEntity.ok(result);
        }
        catch(Exception e) {
            log.error("Error when register new user : " + e.getMessage());
            result.setData(null);
            result.setMessage("Registration failed");
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @KafkaListener(topics = "register-response-topic", groupId = "auth-service-group")
    public void listenRegisterResponse(String response) {
        try {
            var registerResponse = objectMapper.readValue(response, RegisterResponse.class);
            completeRequest(registerResponse);
        }
        catch(Exception e) {
            log.error("Error when read response for new user : " + e.getMessage());
        }
    }

    public void completeRequest(RegisterResponse response) {
        CompletableFuture<RegisterResponse> future = pendingRequests.remove(response.getCorrelationId());
        if (future != null) {
            future.complete(response);
        }
    }
}
