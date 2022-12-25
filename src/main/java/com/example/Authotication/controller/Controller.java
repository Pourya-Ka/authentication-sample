package com.example.Authotication.controller;

import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.UserAuth;
import com.example.Authotication.service.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("authentication")
public class Controller {
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping()
    public AuthTokensDTO getTokens(@RequestBody UserAuth userAuth) {

        return service.getTokens(userAuth);
    }


    @PostMapping("/refresh_token_valid")
    public AuthTokensDTO refreshTKValid(@RequestParam String refreshToken) throws IllegalAccessException {
        return service.refreshTKValid(refreshToken);
    }
}
