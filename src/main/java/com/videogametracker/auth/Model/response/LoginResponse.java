package com.videogametracker.auth.Model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String errorMessage;
    private String token;

    // kafka related
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String correlationId;
}

