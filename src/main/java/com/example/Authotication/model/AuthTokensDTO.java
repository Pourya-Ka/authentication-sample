package com.example.Authotication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthTokensDTO {
    private String accessToken;
    private String refreshToken;
}
