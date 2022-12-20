package com.example.Authotication.repository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Repository
public class Repository {
    private final static List<UserDetails> APPLICATION_USER = Arrays.asList(
            new User(
                    "HamidReza",
                    "password",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
            ),
            new User(
                    "pourya",
                    "password",
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
            )
    );
    public UserDetails userFindByName(String name) {
        return APPLICATION_USER
                .stream()
                .filter(u -> u.getUsername().equals(name))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("no user was found!"));
    }
}
