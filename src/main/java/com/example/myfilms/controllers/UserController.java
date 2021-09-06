package com.example.myfilms.controllers;

import com.example.myfilms.JwtFilter;
import com.example.myfilms.SecretKey;
import com.example.myfilms.models.Credentials;
import com.example.myfilms.models.User;
import com.example.myfilms.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;


@CrossOrigin
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/user")
    public ResponseEntity<String> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userData = userRepository.findByLogin(auth.getPrincipal().toString());
        if (userData.isPresent()) {
            try {
                User user = userData.get();
                JSONObject response = new JSONObject();
                response.put("name", user.getName());
                response.put("surname", user.getSurname());
                response.put("role", user.getRole());
                return new ResponseEntity<>(response.toString(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Optional<User> userData = userRepository.findByLogin(user.getLogin());
        if (userData.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        try {
            userRepository.save(new User(user.getLogin(), passwordEncoder.encode(user.getPassword()), user.getName(), user.getSurname()));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody Credentials credentials) {
        Optional<User> userData = userRepository.findByLogin(credentials.getLogin());

        if (userData.isPresent()) {
            try {
                User user = userData.get();

                if (passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
                    String token = JwtFilter.createToken(credentials.getLogin(), user.getRole());

                    JSONObject response = new JSONObject();
                    response.put("name",user.getName());
                    response.put("surname", user.getSurname());
                    response.put("role", user.getRole());
                    response.put("token", token);
                    return new ResponseEntity<>(response.toString(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
