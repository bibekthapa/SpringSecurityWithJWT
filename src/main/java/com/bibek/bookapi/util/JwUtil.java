package com.bibek.bookapi.util;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwUtil {

    private final String secure_key = "mysecretkeymysecretkeymysecretkey12";
    Key key = Keys.hmacShaKeyFor(secure_key.getBytes());

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secure_key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails){

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // username taken 
                .claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date()) // issued date
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60)) // expirty date
                .signWith(key,SignatureAlgorithm.HS256) // signed
                .compact();
             }

     public String generateToken(String email ){

        return Jwts.builder()
                .setSubject(email) // username taken 
                .setIssuedAt(new Date()) // issued date
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60)) // expirty date
                .signWith(key,SignatureAlgorithm.HS256) // signed
                .compact();
             }
    
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userName = extractUsername(token);
        return userName.equals(userDetails.getUsername());
    }
}
