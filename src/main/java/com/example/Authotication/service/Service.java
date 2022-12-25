package com.example.Authotication.service;

import com.example.Authotication.jwt.JwtUtils;
import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.UserAuth;
import com.example.Authotication.repository.Repository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {
    private Repository repository;
    private AuthenticationManager manager;
    private final JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Autowired
    public Service(Repository repository, AuthenticationManager manager, JwtUtils jwtUtils) {
        this.repository = repository;
        this.manager = manager;
        this.jwtUtils = jwtUtils;
    }

    public AuthTokensDTO getTokens(UserAuth request) {
        final UserDetails user = repository.userFindByName(request.getName());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return new AuthTokensDTO(jwtUtils.generateAccessToken(user),
                    jwtUtils.generateRefreshToken(user)
            );
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "your password wrong!");


    }

    public AuthTokensDTO refreshTKValid(String refreshToken) throws IllegalAccessException {
        final UserDetails userDetails = repository.userFindByName(jwtUtils.extractUsername(refreshToken));

        if (jwtUtils.validateToken(refreshToken, userDetails) &&
                jwtUtils.findByID(refreshToken)
        )
            return new AuthTokensDTO(jwtUtils.generateAccessToken(userDetails), jwtUtils.generateRefreshToken(userDetails));

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "your password wrong!");
    }
}
