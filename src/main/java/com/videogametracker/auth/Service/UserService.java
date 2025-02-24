package com.videogametracker.auth.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videogametracker.auth.Model.request.LoginRequest;
import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.BaseResponse;
import com.videogametracker.auth.Model.response.LoginResponse;
import com.videogametracker.auth.Model.response.RegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
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


    private final ConcurrentHashMap<String, CompletableFuture<RegisterResponse>> pendingRequestRegister = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<LoginResponse>> pendingRequestLogin = new ConcurrentHashMap<>();

    public ResponseEntity<BaseResponse> registerUser(RegisterRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            CompletableFuture<RegisterResponse> futureResponse = new CompletableFuture<>();
            request.setCorrelationId(UUID.randomUUID().toString());

            // give id for kafka
            pendingRequestRegister.put(request.getCorrelationId(), futureResponse);
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

    public LoginResponse getUserByUsername(LoginRequest request) {
        try {
            CompletableFuture<LoginResponse> futureResponse = new CompletableFuture<>();
            request.setCorrelationId(UUID.randomUUID().toString());

            // give id for kafka
            pendingRequestLogin.put(request.getCorrelationId(), futureResponse);
            var reqString = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("login-request-topic", reqString);

            return futureResponse.get(5, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            log.error("Error in getUserByUsername : " + e.getMessage());
            return LoginResponse.builder().errorMessage("Something wrong happened").build();
        }
    }

    @KafkaListener(topics = "register-response-topic", groupId = "auth-service-group")
    public void listenRegisterResponse(String response) {
        try {
            var registerResponse = objectMapper.readValue(response, RegisterResponse.class);
            completeRequest(registerResponse, pendingRequestRegister, registerResponse.getCorrelationId());
        }
        catch(Exception e) {
            log.error("Error in listenRegisterResponse : " + e.getMessage());
        }
    }

    @KafkaListener(topics = "login-response-topic", groupId = "auth-service-group")
    public void listenGetLoginResponse(String response) {
        try {
            var resp = objectMapper.readValue(response, LoginResponse.class);
            completeRequest(resp, pendingRequestLogin, resp.getCorrelationId());
        }
        catch(Exception e) {
            log.error("Error in listenGetLoginResponse : " + e.getMessage());
        }
    }

    public<T> void completeRequest(T response, Map<String, CompletableFuture<T>> pendingRequests, String correlationId) {
        CompletableFuture<T> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }
}
