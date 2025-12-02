package com.example.Sistema_Biblioteca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(csrf -> csrf.disable()) // Desativa CSRF (Evita erro 403 em formulários POST)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
        .authorizeHttpRequests(authorize -> authorize
            // 1. RECURSOS PÚBLICOS (CSS, JS, IMAGENS)
            .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
            
            // 2. FERRAMENTAS DE DEV (SWAGGER, H2)
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/h2-console/**").permitAll()

            // 3. LOGIN E REGISTRO
            .requestMatchers("/auth/**", "/login").permitAll()

            // 4. FRONT-END (THYMELEAF) - LIBERAR GERAL
            // Atenção: O "/**" no final é crucial para permitir /livros/novo, /livros/1/editar, etc.
            .requestMatchers("/", "/index").permitAll()
            .requestMatchers("/livros/**").permitAll()      
            .requestMatchers("/usuarios/**").permitAll()    
            .requestMatchers("/emprestimos/**").permitAll() 
            .requestMatchers("/reservas/**").permitAll()    
            .requestMatchers("/relatorios/**").permitAll()  

            // 5. API REST (SEGURANÇA REFORÇADA)
            // Aqui aplicamos as regras de negócio
            .requestMatchers(HttpMethod.POST, "/api/livros").hasRole("ADMIN") // Só Admin cria via API
            .requestMatchers(HttpMethod.DELETE, "/api/livros/**").hasRole("ADMIN") // Só Admin deleta via API
            .requestMatchers("/api/**").authenticated() // O resto da API exige login

            // 6. O RESTO
            .anyRequest().permitAll() // Em caso de dúvida, libera para não travar seu teste
        )
        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}