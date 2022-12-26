package com.example.Authotication.repository;

import com.example.Authotication.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static com.example.Authotication.config.UserRole.*;

@org.springframework.stereotype.Repository
public class Repository {
    private List<User> users = new ArrayList<>();
    static private Long ID = 3L;

    public Repository() {
        users.add(new User(1l,
                "HamidReza",
                "$2a$12$/UH.nG4w7ZcOAlfh7.ToMe/q49D0Op8dO3J2z/F6xT4j.Z0fVShUe",
                ADMIN
        ));
        users.add(new User(
                2l,
                "pourya",
                "$2a$12$/UH.nG4w7ZcOAlfh7.ToMe/q49D0Op8dO3J2z/F6xT4j.Z0fVShUe",
                USER
        ));
    }


    public User userFindByName(String name) {
        User user = users
                .stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("no user was found!"));

        return user;
    }

    public User userFindById(Long id) {
        return users
                .stream()
                .filter(u -> u.getId() == (id))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("no user was found!"));
    }

    public List<User> findAll() {
        return users;
    }

    public void saveUser(User user) {
        user.setId(ID);
        ID++;
        user.setRole(ADMIN);
        try {
            users.add(user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UserDetails> userDetails() {
        List<UserDetails> userDetails = new ArrayList<>();
        for (User user : users) {
            userDetails.add(org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .authorities(user.getRole().name())
                    .build());
        }
        return userDetails;
    }

//        return APPLICATION_USER
//                .stream()
//                .filter(u -> password != null ? u.getUsername().equalsIgnoreCase(name) && u.getPassword().equals(password)
//                        : u.getUsername().equalsIgnoreCase(name))
//                .findFirst()
//                .orElseThrow(() -> new UsernameNotFoundException("no user was found!"));
//    }
}
