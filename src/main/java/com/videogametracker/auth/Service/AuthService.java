package com.videogametracker.auth.Service;

import com.videogametracker.auth.Config.JwtConfig;
import com.videogametracker.auth.Model.request.LoginRequest;
import com.videogametracker.auth.Model.request.RegisterRequest;
import com.videogametracker.auth.Model.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    PasswordEncoder passwordEncoder;

    public ResponseEntity<BaseResponse> login(LoginRequest request) {
        BaseResponse result = new BaseResponse();
        try {
            var user = userService.getUserAuth(request.getUsername());
            if (user.getErrorMessage() != null && !user.getErrorMessage().equals("")) throw new RuntimeException("Invalid credentials");
                if (user.getUserId() != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    user.setToken(jwtConfig.generateToken(request.getUsername()));
                    result.setData(user);
                    result.setMessage("Login success");
                    result.setStatus(HttpStatus.OK.value());
                    return ResponseEntity.ok(result);
                }
            throw new RuntimeException("Invalid credentials");
        }
        catch(Exception e) {
            log.error("Error login : " + e.getMessage());
            result.setData(null);
            result.setMessage("Login failed");
            result.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(result);
        }
    }

    public ResponseEntity<BaseResponse> registerUser(RegisterRequest request) {
        var registerResponse = userService.getUserRegisterInfo(request);
        BaseResponse result = new BaseResponse();
        result.setData(registerResponse);
        result.setMessage("Registration success");
        result.setStatus(HttpStatus.OK.value());
        return ResponseEntity.ok(result);
    }
}
