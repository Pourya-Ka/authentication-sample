package com.example.Authotication.controller;

import com.example.Authotication.model.AuthTokensDTO;
import com.example.Authotication.model.User;
import com.example.Authotication.service.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("authentication")
public class Controller {
    private Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping("users")
    public List<User> getUsers(){
        return service.getUsers();
    }
    @GetMapping("users/{user_id}")
    public User getUsers(@PathVariable(value = "user_id",required = false) Long user_id){
        return service.getUserById(user_id);
    }
    @PostMapping("add_user")
    public List<User> addUser(@RequestBody User user){
       return service.addUser(user);
    }

    @PostMapping()
    public AuthTokensDTO getTokens(@RequestBody User user) {

        return service.getTokens(user);
    }


    @PostMapping("/refresh_token_valid")
    public AuthTokensDTO refreshTKValid(@RequestParam String refreshToken) throws IllegalAccessException {
        return service.refreshTKValid(refreshToken);
    }
}
