package com.bibek.bookapi.filter;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bibek.bookapi.service.CuustomUserDetailsService;
import com.bibek.bookapi.util.JwUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    @Autowired private JwUtil jwtUtil;
    @Autowired private CuustomUserDetailsService userDetailsService;
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            // Get header
            //Get userName
            //Is token valid ? 
            // Reason to do this is to make sure http request is verified once
            // Makes sure the user is not already authenticated
            final String authHeader = request.getHeader("Authorization");
            String userName = null , jwt = null ;

            if(authHeader != null && authHeader.startsWith("Bearer "))
            {
                jwt = authHeader.substring(7);
                userName = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted username: " + userName);

            }

            if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null ){


                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if(jwtUtil.isTokenValid(jwt, userDetails)) {
                System.out.println("JWT is valid. Setting authentication...");

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
            else{
                        System.out.println("Invalid JWT token.");

            }
                
            }
            filterChain.doFilter(request,response);


          }

    
}
