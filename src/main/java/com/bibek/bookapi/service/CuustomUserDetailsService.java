package com.bibek.bookapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bibek.bookapi.model.AppUser;
import com.bibek.bookapi.repository.UserRepository;

@Component
public class CuustomUserDetailsService implements UserDetailsService{

   private final UserRepository userRepository;
   private final PasswordEncoder encoder;

   

   public CuustomUserDetailsService (UserRepository userRepository , @Lazy PasswordEncoder encoder){
    this.userRepository = userRepository;
    this.encoder = encoder;
   }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        return User.builder().
        username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole().replace("ROLE_",""))
        .build();
            }
    

}
    

