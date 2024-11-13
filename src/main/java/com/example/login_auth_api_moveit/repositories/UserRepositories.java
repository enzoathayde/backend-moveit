package com.example.login_auth_api_moveit.repositories;

import com.example.login_auth_api_moveit.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositories extends JpaRepository<User,String> {
    Optional<User> findByEmail(String email);
}
