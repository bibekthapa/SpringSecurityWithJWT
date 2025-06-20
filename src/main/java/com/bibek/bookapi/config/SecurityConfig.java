package com.bibek.bookapi.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bibek.bookapi.filter.JwtAuthFilter;
import com.bibek.bookapi.service.CustomOauth2UserService;
import com.bibek.bookapi.service.CuustomUserDetailsService;
import com.bibek.bookapi.util.JwUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

@Autowired
   private CuustomUserDetailsService userDetailsService;

@Autowired
private JwtAuthFilter jwtAuthFilter;

private final JwUtil jwUtil;

private final CustomOauth2UserService oauth2UserService;

public SecurityConfig(JwUtil jwUtil , CustomOauth2UserService oauth2UserService){
    this.jwUtil             = jwUtil;
    this.oauth2UserService = oauth2UserService;
}

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
         csrf(csrf -> csrf.disable())
         .headers(headers -> headers.frameOptions(frame -> frame.disable()))
         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/register").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user/**").hasAnyRole("ADMIN","USER")
                                .anyRequest().authenticated()
                              )
                              .oauth2Login(oauth2 -> oauth2.userInfoEndpoint(
                                userInfo -> userInfo.userService(oauth2UserService)
                                ).successHandler((request,response,authentication) ->{
                                    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                                    String email = oauthToken.getPrincipal().getAttribute("email");
                                    String jwtToken = jwUtil.generateToken(email);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");
                                    response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");

                                })
                                
                                )
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
                             

                
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{

       return config.getAuthenticationManager();
    }

    //@Bean
    public AuthenticationManager authenticationManager1(HttpSecurity http) throws Exception{

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
                

    }


   //@Bean
   //To check if the encoding and username password is working or not. hardcoded method
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    UserDetails user = User.builder()
        .username("testuser")
        .password(passwordEncoder.encode("testpass")) // use your encoder
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(user);
}

}
