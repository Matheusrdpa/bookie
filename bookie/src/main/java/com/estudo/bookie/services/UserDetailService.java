package com.estudo.bookie.services;

import com.estudo.bookie.entities.CustomUserDetails;
import com.estudo.bookie.entities.User;
import com.estudo.bookie.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private UserRepository userRepository;
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::mapUserToDetails).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetails mapUserToDetails(User user) {
        return new CustomUserDetails(user);
    }
}
