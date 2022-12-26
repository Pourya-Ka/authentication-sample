package com.example.Authotication.model;

import com.example.Authotication.config.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    @JsonProperty()
    private Long id;
    private String username;
    private String password;
    private UserRole role;
}
