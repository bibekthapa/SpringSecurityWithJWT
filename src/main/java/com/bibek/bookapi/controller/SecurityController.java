package com.bibek.bookapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bibek.bookapi.model.AppUser;
import com.bibek.bookapi.repository.UserRepository;
import com.bibek.bookapi.util.JwUtil;

import lombok.Data;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class SecurityController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwUtil jUtil;

    @GetMapping("/user/public")
    public String publicApi() {
        return "This is a public API endpoint accessible without authentication.";
    }

    @GetMapping("/admin/private")
    public String privateApi()  {
        return "This is a private API endpoint accessible only with authentication.";
    }


     @GetMapping("/secure/user")
    public Map<String, Object> user(OAuth2AuthenticationToken auth) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", auth.getPrincipal().getAttribute("name"));
        map.put("email", auth.getPrincipal().getAttribute("email"));
        return map;
    }

    @PostMapping("/register")
    public String register(@RequestBody AppUser user) {
        if(userRepo.findByUsername(user.getUsername()).isPresent()){
            return "UserName exists";
        }

        userRepo.save(
            AppUser.builder()
            .username(user.getUsername())
            .password(encoder.encode(user.getPassword()))
            .role(user.getRole())
            .build()
        );

       return "Registered";
    }

    @PostMapping("/login")
    public ResponseEntity<String> postMethodName(@RequestBody AuthRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jUtil.generateToken(user);
        return ResponseEntity.ok(jwt);
    }
    
    @Data
   static class AuthRequest{
        private String username;
        private String password;
    }
    
}