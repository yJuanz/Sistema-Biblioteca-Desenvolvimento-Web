package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.dto.AuthenticationDTO;
import com.example.Sistema_Biblioteca.dto.LoginResponseDTO;
import com.example.Sistema_Biblioteca.dto.RegisterDTO;
import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.repository.UsuarioRepository;
import com.example.Sistema_Biblioteca.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador responsável pelos endpoints públicos de autenticação.
 * Aqui gerenciamos o Login (geração de token) e o Registro de novos usuários.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    // Gerenciador de autenticação do Spring Security (verifica as credenciais)
    @Autowired
    private AuthenticationManager authenticationManager;

    // Repositório para acessar dados do usuário no banco
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Serviço responsável pela lógica de geração e validação do JWT
    @Autowired
    private TokenService tokenService;

    /**
     * Endpoint de LOGIN
     * Recebe email e senha, valida e retorna o Token JWT via Cookie e Body.
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data, HttpServletResponse response) {
        
        // 1. Encapsula as credenciais (login/senha) recebidas do front-end
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
        
        // 2. O Spring Security verifica no banco se a senha bate com o hash salvo
        var auth = authenticationManager.authenticate(usernamePassword);

        // 3. Se a autenticação passar, geramos o Token JWT assinado
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        // 4. CONFIGURAÇÃO DE COOKIE (Segurança Extra)
        // Armazenamos o token num Cookie HttpOnly para que o navegador gerencie a sessão.
        Cookie cookie = new Cookie("BIBLIOTECA_TOKEN", token);
        
        // HttpOnly: Impede que scripts JavaScript (XSS) leiam o token
        cookie.setHttpOnly(true); 
        
        // Secure: Em produção (HTTPS), isso deve ser true para trafegar apenas criptografado
        cookie.setSecure(false); 
        
        // Path: O cookie é válido para toda a aplicação
        cookie.setPath("/");      
        
        // MaxAge: Define a expiração do cookie (2 horas, igual ao token)
        cookie.setMaxAge(60 * 60 * 2); 
        
        // Adiciona o cookie na resposta HTTP que vai para o navegador
        response.addCookie(cookie);

        // Retorna 200 OK com o token também no corpo (para flexibilidade de API)
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    /**
     * Endpoint de REGISTRO
     * Cria um novo usuário no banco com a senha criptografada.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        
        // 1. Validação: Impede duplicidade de emails no sistema
        if (this.usuarioRepository.findByEmail(data.email()).isPresent())
            return ResponseEntity.badRequest().build();

        // 2. Criptografia: A senha NUNCA é salva em texto plano. Usamos BCrypt.
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());
        
        // 3. Criação da entidade Usuário com os dados do DTO e a senha hash
        Usuario newUser = new Usuario(data.nome(), data.matricula(), data.email(), encryptedPassword, data.tipo());

        // 4. Persistência no banco de dados
        this.usuarioRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}