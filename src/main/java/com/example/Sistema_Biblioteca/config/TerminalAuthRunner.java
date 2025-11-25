package com.example.Sistema_Biblioteca.config;

import com.example.Sistema_Biblioteca.model.TipoUsuario;
import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.repository.UsuarioRepository;
import com.example.Sistema_Biblioteca.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Scanner;

@Configuration
public class TerminalAuthRunner implements CommandLineRunner {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public void run(String... args) throws Exception {
        // 1. Garantir que existe um ADMIN para teste
        criarAdminSeNaoExistir();

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=================================================");
        System.out.println("   SISTEMA DE BIBLIOTECA - AUTENTICAÇÃO VIA TERMINAL");
        System.out.println("=================================================");
        System.out.println("OBS: A aplicação WEB continua rodando em segundo plano.");
        System.out.println("Para pular o login no terminal e ver os logs, digite 'sair'.");
        System.out.println("-------------------------------------------------");

        while (true) {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Digite seu Email (ex: admin@biblioteca.com): ");
            String email = scanner.nextLine();

            if (email.equalsIgnoreCase("sair")) {
                System.out.println("Saindo do modo interativo...");
                break;
            }

            System.out.print("Digite sua Senha (ex: 123456): ");
            String senha = scanner.nextLine();

            try {
                // 2. Tenta autenticar manualmente usando o Manager do Spring Security
                UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(email, senha);
                Authentication auth = authenticationManager.authenticate(usernamePassword);

                // 3. Se deu certo, gera o token
                String token = tokenService.gerarToken((Usuario) auth.getPrincipal());

                System.out.println("\n✅ AUTENTICAÇÃO BEM SUCEDIDA!");
                System.out.println("-------------------------------------------------");
                System.out.println("SEU TOKEN BEARER:");
                System.out.println(token);
                System.out.println("-------------------------------------------------");
                System.out.println("Copie o token acima para usar no Header 'Authorization' das suas requisições.");
                
                // Pergunta se quer continuar logando ou liberar o terminal
                System.out.print("\nDeseja fazer outro login? (S/N): ");
                String continuar = scanner.nextLine();
                if (continuar.equalsIgnoreCase("N")) {
                    break;
                }

            } catch (AuthenticationException e) {
                System.out.println("\n❌ FALHA NO LOGIN: Usuário ou senha inválidos.");
            } catch (Exception e) {
                System.out.println("\n❌ ERRO: " + e.getMessage());
            }
        }
    }

    private void criarAdminSeNaoExistir() {
        if (usuarioRepository.findByEmail("admin@biblioteca.com").isEmpty()) {
            String senhaCriptografada = new BCryptPasswordEncoder().encode("123456");
            Usuario admin = new Usuario(
                    "Administrador",
                    "ADMIN001",
                    "admin@biblioteca.com",
                    senhaCriptografada,
                    TipoUsuario.FUNCIONARIO
            );
            usuarioRepository.save(admin);
            System.out.println(">> Usuário ADMIN criado automaticamente: admin@biblioteca.com / 123456");
        }
    }
}