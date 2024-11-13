package com.example.login_auth_api_moveit.infra.security;

import com.example.login_auth_api_moveit.domain.user.User;
import com.example.login_auth_api_moveit.repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


// ONDE SERÁ CONSULTADO OS USUÁRIOS
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepositories userRepositories;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepositories.findByEmail(username).orElseThrow(() ->  new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
