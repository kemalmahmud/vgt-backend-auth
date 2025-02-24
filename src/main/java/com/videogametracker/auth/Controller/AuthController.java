package com.videogametracker.auth.Controller;


import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.RegisterResponse;
import com.videogametracker.auth.Service.grpc.UserGrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserGrpcService userGrpcService;

    @PostMapping("/register")
    public RegisterResponse registerNewUser(@RequestBody RegisterRequest request) {
        return userGrpcService.registerUser(request);
    }
}
