package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.TipoUsuario;
import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.ReservaService; // Serviço de Reservas
import com.example.Sistema_Biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller responsável pelos Formulários de Criação (Novos registros)
 * e pelas ações de negócio (Realizar Empréstimo, Reserva, Devolução).
 */
@Controller
public class FormularioController {

    @Autowired private LivroService livroService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private EmprestimoService emprestimoService;
    @Autowired private ReservaService reservaService; // Injeção essencial para reservas

    private String encode(String text) {
        try {
            return URLEncoder.encode(text != null ? text : "", StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "erro";
        }
    }

    // =========================================================
    // CADASTRO DE LIVROS (COMPLETO)
    // =========================================================

    @GetMapping("/livros/novo")
    public String mostrarFormularioLivro(Model model) {
        return "form-livro";
    }

    @PostMapping("/livros/novo")
    public String salvarLivro(@RequestParam String titulo,
                              @RequestParam String isbn,
                              @RequestParam Integer anoPublicacao,
                              @RequestParam Integer quantidadeExemplares,
                              @RequestParam String nomeAutor,      // <--- Novo campo
                              @RequestParam String nomeCategoria,  // <--- Novo campo
                              @RequestParam String nomeEditora) {  // <--- Novo campo
        try {
            // Chama o método 'criarLivroCompleto' que lida com a criação automática
            // de Autor, Categoria e Editora se eles não existirem.
            livroService.criarLivroCompleto(titulo, isbn, anoPublicacao, quantidadeExemplares, nomeAutor, nomeCategoria, nomeEditora);
            
            return "redirect:/livros?sucesso=" + encode("Livro cadastrado com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/livros/novo?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // CADASTRO DE USUÁRIOS
    // =========================================================

    @GetMapping("/usuarios/novo")
    public String mostrarFormularioUsuario(Model model) {
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        return "form-usuario";
    }

    @PostMapping("/usuarios/novo")
    public String salvarUsuario(@RequestParam String nome,
                                @RequestParam String matricula,
                                @RequestParam String email,
                                @RequestParam TipoUsuario tipo) {
        try {
            var usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setMatricula(matricula);
            usuario.setEmail(email);
            usuario.setTipo(tipo);
            
            // Segurança: Define uma senha padrão criptografada (BCrypt) para novos usuários
            usuario.setSenha(new BCryptPasswordEncoder().encode("123456")); 

            usuarioService.criar(usuario);
            return "redirect:/usuarios?sucesso=" + encode("Usuário cadastrado com sucesso");
        } catch (Exception e) {
            return "redirect:/usuarios/novo?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // OPERAÇÃO: REALIZAR EMPRÉSTIMO
    // =========================================================

    @GetMapping("/emprestimos/novo")
    public String mostrarFormularioEmprestimo(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            // Filtra apenas livros que têm estoque disponível para não errar no form
            model.addAttribute("livrosDisponiveis", livroService.listarDisponiveis());
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
        return "form-emprestimo";
    }

    @PostMapping("/emprestimos/novo")
    public String salvarEmprestimo(@RequestParam Long usuarioId,
                                   @RequestParam Long livroId) {
        try {
            // Executa a lógica de empréstimo (baixa no estoque, cria registro)
            emprestimoService.realizarEmprestimo(usuarioId, livroId);
            return "redirect:/emprestimos?sucesso=" + encode("Empréstimo realizado com sucesso");
        } catch (Exception e) {
            // Retorna o erro para ser exibido no pop-up do HTML
            return "redirect:/emprestimos/novo?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // OPERAÇÃO: REALIZAR RESERVA
    // =========================================================

    @GetMapping("/reservas/nova")
    public String mostrarFormularioReserva(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("livros", livroService.listarTodos());
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
        return "form-reserva";
    }

    @PostMapping("/reservas/nova")
    public String salvarReserva(@RequestParam Long usuarioId,
                                @RequestParam Long livroId) {
        try {
            // Chama o serviço de Reserva para persistir no banco
            reservaService.realizarReserva(usuarioId, livroId);
            
            return "redirect:/reservas?sucesso=" + encode("Reserva realizada com sucesso");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/reservas/nova?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // OPERAÇÃO: DEVOLUÇÃO
    // =========================================================

    @PostMapping("/emprestimos/{id}/devolver")
    public String devolverEmprestimo(@PathVariable Long id) {
        try {
            // Processa a devolução e cálculo de multa (se houver atraso)
            emprestimoService.devolverLivro(id);
            return "redirect:/emprestimos?sucesso=" + encode("Livro devolvido com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/emprestimos?error=" + encode("Erro na devolução: " + e.getMessage());
        }
    }
}