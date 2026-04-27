package com.marjane.ems.Services;

import com.marjane.ems.DAL.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService implementation.
 * Loads user authentication data from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user by username from database.
     * 
     * @param username The username to lookup
     * @return UserDetails with user information and authorities
     * @throws UsernameNotFoundException If user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
            .findByUsername(username)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * Load user by EID (alternative lookup method).
     * 
     * @param eid The EID to lookup
     * @return UserDetails with user information and authorities
     * @throws UsernameNotFoundException If user not found
     */
    public UserDetails loadUserByEid(String eid) throws UsernameNotFoundException {
        return userRepository
            .findByEid(eid)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with EID: " + eid));
    }

    /**
     * Load user by email (alternative lookup method).
     * 
     * @param email The email to lookup
     * @return UserDetails with user information and authorities
     * @throws UsernameNotFoundException If user not found
     */
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository
            .findByEmail(email)
            .map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
