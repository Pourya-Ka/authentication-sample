package com.example.Authotication.service;

import com.example.Authotication.jwt.JwtUtils;
import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.UserAuth;
import com.example.Authotication.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {
    private Repository repository;
    private final AuthenticationManager manager;
    private final JwtUtils jwtUtils;

    @Autowired
    public Service(Repository repository, AuthenticationManager manager, JwtUtils jwtUtils) {
        this.repository = repository;
        this.manager = manager;
        this.jwtUtils = jwtUtils;
    }

    public AuthTokensDTO getTokens(UserAuth request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getName(), request.getPassword())
        );
        final UserDetails user = repository.userFindByName(request.getName());


        AuthTokensDTO tokensDTO = new AuthTokensDTO();
        tokensDTO.setAccessToken(jwtUtils.generateToken(user,5));
        tokensDTO.setRefreshToken(jwtUtils.generateToken(user,10));
        return tokensDTO;
    }
}
