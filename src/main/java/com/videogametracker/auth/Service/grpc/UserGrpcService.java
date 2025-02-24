//package com.videogametracker.auth.Service.grpc;
//
//import com.proto.UserRegisterRequest;
//import com.proto.UserRegisterResponse;
//import com.proto.UserServiceGrpc;
//import com.videogametracker.auth.Model.request.RegisterRequest;
//import com.videogametracker.auth.Model.response.BaseResponse;
//import com.videogametracker.auth.Model.response.RegisterResponse;
//import net.devh.boot.grpc.client.inject.GrpcClient;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserGrpcService {
//
//    @GrpcClient("grpc-vgt-service")
//    UserServiceGrpc.UserServiceBlockingStub syncClient;
//
//    public ResponseEntity<BaseResponse> registerUser(RegisterRequest request) {
//        UserRegisterRequest req = UserRegisterRequest.newBuilder()
//                .setUsername(request.getUsername())
//                .setEmail(request.getEmail())
//                .setPassword(request.getPassword())
//                .setFullname(request.getName())
//                .setDescription(request.getDescription())
//                .build();
//        UserRegisterResponse resp = syncClient.registerUser(req);
//        BaseResponse result = new BaseResponse();
//        RegisterResponse registerResponse = RegisterResponse.builder()
//                .userId(resp.getUserId())
//                .build();
//        result.setData(registerResponse);
//        result.setMessage("Registration success");
//        result.setStatus(HttpStatus.OK.value());
//        return ResponseEntity.ok(result);
//    }
//}
