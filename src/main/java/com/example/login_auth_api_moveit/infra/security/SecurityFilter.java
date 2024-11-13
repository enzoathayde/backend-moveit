package com.example.login_auth_api_moveit.infra.security;

import com.example.login_auth_api_moveit.domain.user.User;
import com.example.login_auth_api_moveit.infra.security.TokenService;
import com.example.login_auth_api_moveit.repositories.UserRepositories;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

//  TUDO ISSO AQUI VAI VERIFICAR SE O QUE  O USUÁRIO MANDOU ESTÁ CORRETO, CASO SEJA ELE SALVAR NAS INFORMAÇÕES DA AUTENTICAÇÃo

@Component
public class SecurityFilter extends OncePerRequestFilter {   // RODA EM CADA REQUISIÇÃO
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepositories userRepository;

    // O FILTRO ABAIXO VERIFICA O TOKEN QUE VEIO NA REQUISIÇÃO PARA VALIDÁ-LO

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        //PRECISA VERIFICAR SE NÃO É NULO BASEADO NO TRYCATCH FEITO EM VALIDATE TOKEN

        if(login != null){
            User user = userRepository.findByEmail(login).orElseThrow(() -> new RuntimeException("User Not Found"));  //BUSCA O USUÁRIO NO BANCO DE DADOS, CASO NÃO ENCONTRE JOGA UMA EXCEÇÃO
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));   // CRIA UMA ROLE PRO USUÁRIO
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);   // AQUI É O CONTEXTO DE SEGURANÇA DO SPRING SECURITY, ONDE ELE VALIDOU OU NÃO VALIDOU
        }
        filterChain.doFilter(request, response);
    }

    // RECEBE A REQUISIÇÃO DO USUÁRIO, AQUI VC MANIPULA ONDE ESTÁ O TOKEN

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}