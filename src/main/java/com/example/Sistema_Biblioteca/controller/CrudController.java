package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller principal para operações de CRUD (Create, Read, Update, Delete)
 * Gerencia as requisições vindas das páginas de listagem e detalhes.
 */
@Controller
public class CrudController {

    @Autowired private LivroService livroService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private EmprestimoService emprestimoService;

    // Método utilitário para codificar mensagens de erro na URL (ex: espaços viram %20)
    private String encode(String text) {
        try {
            return URLEncoder.encode(text != null ? text : "", StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return "erro";
        }
    }

    // =========================================================
    // AÇÕES PARA LIVROS
    // =========================================================

    /**
     * Exibe a página de detalhes de um livro específico.
     */
    @GetMapping("/livros/{id}")
    public String visualizarLivro(@PathVariable Long id, Model model) {
        try {
            var livro = livroService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
            model.addAttribute("livro", livro); // Passa o objeto para o HTML
            return "detalhes-livro";
        } catch (Exception e) {
            return "redirect:/livros?error=" + encode(e.getMessage());
        }
    }

    /**
     * Carrega o formulário de edição com os dados atuais do livro.
     */
    @GetMapping("/livros/{id}/editar")
    public String editarLivro(@PathVariable Long id, Model model) {
        try {
            var livro = livroService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
            model.addAttribute("livro", livro);
            return "form-livro-editar";
        } catch (Exception e) {
            return "redirect:/livros?error=" + encode(e.getMessage());
        }
    }

    /**
     * Processa a atualização do livro.
     * Recebe todos os campos do formulário, incluindo Autor, Categoria e Editora.
     */
    @PostMapping("/livros/{id}/editar")
    public String atualizarLivro(@PathVariable Long id,
                                 @RequestParam String titulo,
                                 @RequestParam String isbn,
                                 @RequestParam Integer anoPublicacao,
                                 @RequestParam Integer quantidadeExemplares,
                                 @RequestParam String nomeAutor,      // Recebido do input 'nomeAutor'
                                 @RequestParam String nomeCategoria,  // Recebido do input 'nomeCategoria'
                                 @RequestParam String nomeEditora) {  // Recebido do input 'nomeEditora'
        try {
            // Chama o serviço robusto para atualizar tudo
            livroService.atualizarCompleto(id, titulo, isbn, anoPublicacao, quantidadeExemplares, nomeAutor, nomeCategoria, nomeEditora);
            
            return "redirect:/livros?sucesso=" + encode("Livro atualizado com sucesso");
        } catch (Exception e) {
            e.printStackTrace(); // Ajuda no debug
            return "redirect:/livros/" + id + "/editar?error=" + encode(e.getMessage());
        }
    }

    /**
     * Processa a exclusão de um livro.
     */
    @PostMapping("/livros/{id}/excluir")
    public String excluirLivro(@PathVariable Long id) {
        try {
            livroService.deletar(id); // Chama a exclusão segura no service
            return "redirect:/livros?sucesso=" + encode("Livro excluído com sucesso");
        } catch (Exception e) {
            // Se falhar (ex: livro emprestado), mostra o erro na lista
            return "redirect:/livros?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // AÇÕES PARA USUÁRIOS
    // =========================================================

    @GetMapping("/usuarios/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        try {
            var usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            return "detalhes-usuario";
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + encode(e.getMessage());
        }
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try {
            var usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            // Passa os valores do Enum para o select do HTML
            model.addAttribute("tiposUsuario", com.example.Sistema_Biblioteca.model.TipoUsuario.values());
            return "form-usuario";
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + encode(e.getMessage());
        }
    }

    @PostMapping("/usuarios/{id}/editar")
    public String atualizarUsuario(@PathVariable Long id,
                                   @RequestParam String nome,
                                   @RequestParam String matricula,
                                   @RequestParam String email,
                                   @RequestParam com.example.Sistema_Biblioteca.model.TipoUsuario tipo) {
        try {
            var usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            usuario.setNome(nome);
            usuario.setMatricula(matricula);
            usuario.setEmail(email);
            usuario.setTipo(tipo);

            usuarioService.atualizar(id, usuario);
            return "redirect:/usuarios?sucesso=" + encode("Usuário atualizado com sucesso");
        } catch (Exception e) {
            return "redirect:/usuarios/" + id + "/editar?error=" + encode(e.getMessage());
        }
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return "redirect:/usuarios?sucesso=" + encode("Usuário excluído com sucesso");
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + encode(e.getMessage());
        }
    }

    // =========================================================
    // AÇÕES PARA EMPRÉSTIMOS
    // =========================================================

    @GetMapping("/emprestimos/{id}")
    public String visualizarEmprestimo(@PathVariable Long id, Model model) {
        // Redireciona para a lista geral, pois não temos página de detalhes de empréstimo ainda
        try {
            return "redirect:/emprestimos";
        } catch (Exception e) {
            return "redirect:/emprestimos?error=" + encode(e.getMessage());
        }
    }
}