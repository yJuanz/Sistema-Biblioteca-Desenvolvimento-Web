package com.example.Sistema_Biblioteca.config;

import com.example.Sistema_Biblioteca.repository.UsuarioRepository;
import com.example.Sistema_Biblioteca.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);
        
        if (token != null) {
            var login = tokenService.validateToken(token);

            if (!login.isEmpty()) {
                UserDetails usuario = usuarioRepository.findByEmail(login).orElse(null);

                if (usuario != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        // 1. Tenta pegar do Header (Para Postman/Swagger)
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null) return authHeader.replace("Bearer ", "");

        // 2. --- NOVO: Tenta pegar do Cookie (Para Navegador/Thymeleaf) ---
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("BIBLIOTECA_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}