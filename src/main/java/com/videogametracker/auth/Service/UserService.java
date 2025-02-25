package com.videogametracker.auth.Service;

import com.proto.*;
import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.BaseResponse;
import com.videogametracker.auth.Model.response.LoginResponse;
import com.videogametracker.auth.Model.response.RegisterResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @GrpcClient("grpc-vgt-user-service")
    UserServiceGrpc.UserServiceBlockingStub syncClient;

    public LoginResponse getUserAuth(String userId) {
        UserAuthRequestGrpc req = UserAuthRequestGrpc.newBuilder()
                .setUsername(userId)
                .build();
        UserAuthResponseGrpc resp = syncClient.getUserAuthGrpc(req);
        return LoginResponse.builder()
                .username(resp.getUsername())
                .password(resp.getPassword())
                .errorMessage(resp.getErrorMessage())
                .userId(resp.getUserId())
                .build();
    }

    public RegisterResponse getUserRegisterInfo(RegisterRequest request) {
        UserRegisterRequestGrpc req = UserRegisterRequestGrpc.newBuilder()
                .setUsername(request.getUsername())
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setFullname(request.getName())
                .setDescription(request.getDescription())
                .build();
        UserRegisterResponseGrpc resp = syncClient.registerUserGrpc(req);
        return RegisterResponse.builder()
                .userId(resp.getUserId())
                .build();
    }
}