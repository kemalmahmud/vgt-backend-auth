package com.videogametracker.auth.Controller;


import com.videogametracker.auth.Model.request.LoginRequest;
import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.BaseResponse;
import com.videogametracker.auth.Service.AuthService;
import com.videogametracker.auth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> registerNewUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
