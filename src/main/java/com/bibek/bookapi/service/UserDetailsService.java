package com.bibek.bookapi.service;

import com.bibek.bookapi.model.AppUser;

public interface UserDetailsService {
    
    // Define methods for user details service
    // For example, you might want to load user by username or ID
    AppUser loadUserByUsername(String username);

    // You can also define other methods as needed, such as saving a new user, etc.
    
}
