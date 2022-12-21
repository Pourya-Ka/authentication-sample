package com.example.Authotication.service;

import com.example.Authotication.jwt.JwtUtils;
import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.UserAuth;
import com.example.Authotication.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

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
//        try {
//            manager.authenticate(
//                    new UsernamePasswordAuthenticationToken(request.getName(),request.getPassword())
//            );
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        final UserDetails user = repository.userFindByName(request.getName());
        if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return new AuthTokensDTO(jwtUtils.generateToken(user, 5),
                    jwtUtils.generateToken(user, 10)
            );
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"your password wrong!");



    }
}
