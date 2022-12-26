package com.example.Authotication.service;

import com.example.Authotication.jwt.JwtUtils;
import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.User;
import com.example.Authotication.repository.Repository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class Service {
    private Repository repository;
    private final JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    public Service(Repository repository, JwtUtils jwtUtils) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    public AuthTokensDTO getTokens(User request) {
        final User user = repository.userFindByName(request.getUsername());
        if (passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())
        )
            return new AuthTokensDTO(jwtUtils.generateAccessToken(user),
                    jwtUtils.generateRefreshToken(user)
            );
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "your password wrong!");


    }

    public AuthTokensDTO refreshTKValid(String refreshToken) throws IllegalAccessException {
        final User user = repository.userFindById(
                Long.valueOf(jwtUtils.extractClaim(refreshToken, Claims::getSubject))
        );

        jwtUtils.validateToken(refreshToken, user);
        jwtUtils.findByID(refreshToken);

        return new AuthTokensDTO(jwtUtils.generateAccessToken(user), jwtUtils.generateRefreshToken(user));
    }

    public List<User> getUsers() {
            return repository.findAll();
    }

    public User getUserById(Long user_id) {
        return repository.userFindById(user_id);
    }


    public List<User> addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.saveUser(user);
        return repository.findAll();
    }


}
