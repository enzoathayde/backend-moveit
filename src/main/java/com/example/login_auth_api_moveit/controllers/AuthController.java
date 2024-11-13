package com.example.login_auth_api_moveit.controllers;


import com.example.login_auth_api_moveit.domain.user.User;
import com.example.login_auth_api_moveit.dto.LoginRequestDTO;
import com.example.login_auth_api_moveit.dto.RegisterRequestDTO;
import com.example.login_auth_api_moveit.dto.ResponseDTO;
import com.example.login_auth_api_moveit.infra.security.TokenService;
import com.example.login_auth_api_moveit.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // gera o construtor dessa classe contendo nossos 3 atributos, substitui o @Autowired
public class AuthController {


    private final UserRepositories userRepositories;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepositories.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));

        if(passwordEncoder.matches(body.password(), user.getPassword() )) { // verifica a senha do usuario param1:
            String token = this.tokenService.generateToken(user);

            return ResponseEntity.ok(new ResponseDTO(user.getName(), token));  // ESTÁ COM A SENHA CORRETA
        }

        return ResponseEntity.badRequest().build(); // SENHA ESTÁ INCORRETA

    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {

        Optional<User> user = this.userRepositories.findByEmail(body.email());

       if(user.isEmpty() ) {

           User newUser = new User();

           newUser.setPassword(passwordEncoder.encode(body.password()));
           newUser.setEmail(body.email());
           newUser.setName(body.name());

           this.userRepositories.save(newUser);


               String token = this.tokenService.generateToken(newUser);
                return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));

           }

        return ResponseEntity.badRequest().build(); // SENHA ESTÁ INCORRETA

    }




}
