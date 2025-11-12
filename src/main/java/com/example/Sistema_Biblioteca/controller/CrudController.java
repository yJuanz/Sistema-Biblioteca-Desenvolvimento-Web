package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CrudController {

    @Autowired
    private LivroService livroService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmprestimoService emprestimoService;

    // ========== AÇÕES PARA LIVROS ==========

    @GetMapping("/livros/{id}")
    public String visualizarLivro(@PathVariable Long id, Model model) {
        try {
            var livro = livroService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
            model.addAttribute("livro", livro);
            return "detalhes-livro";
        } catch (Exception e) {
            return "redirect:/livros?error=" + e.getMessage();
        }
    }

    @GetMapping("/livros/{id}/editar")
    public String editarLivro(@PathVariable Long id, Model model) {
        try {
            var livro = livroService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
            model.addAttribute("livro", livro);
            return "form-livro-editar";
        } catch (Exception e) {
            return "redirect:/livros?error=" + e.getMessage();
        }
    }

    @PostMapping("/livros/{id}/editar")
    public String atualizarLivro(@PathVariable Long id,
                                 @RequestParam String titulo,
                                 @RequestParam String isbn,
                                 @RequestParam Integer anoPublicacao,
                                 @RequestParam Integer quantidadeExemplares) {
        try {
            var livro = livroService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

            livro.setTitulo(titulo);
            livro.setIsbn(isbn);
            livro.setAnoPublicacao(anoPublicacao);
            livro.setQuantidadeExemplares(quantidadeExemplares);

            livroService.atualizar(id, livro);
            return "redirect:/livros?sucesso=Livro atualizado com sucesso";
        } catch (Exception e) {
            return "redirect:/livros/" + id + "/editar?error=" + e.getMessage();
        }
    }

    @PostMapping("/livros/{id}/excluir")
    public String excluirLivro(@PathVariable Long id) {
        try {
            livroService.deletar(id);
            return "redirect:/livros?sucesso=Livro excluído com sucesso";
        } catch (Exception e) {
            return "redirect:/livros?error=" + e.getMessage();
        }
    }

    // ========== AÇÕES PARA USUÁRIOS ==========

    @GetMapping("/usuarios/{id}")
    public String visualizarUsuario(@PathVariable Long id, Model model) {
        try {
            var usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            return "detalhes-usuario";
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }

    // === MÉTODO QUE ESTAVA FALTANDO ===
    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        try {
            var usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            model.addAttribute("tiposUsuario", com.example.Sistema_Biblioteca.model.TipoUsuario.values());
            return "form-usuario";  // ← USA form-usuario (template único)
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + e.getMessage();
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
            return "redirect:/usuarios?sucesso=Usuário atualizado com sucesso";
        } catch (Exception e) {
            return "redirect:/usuarios/" + id + "/editar?error=" + e.getMessage();
        }
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return "redirect:/usuarios?sucesso=Usuário excluído com sucesso";
        } catch (Exception e) {
            return "redirect:/usuarios?error=" + e.getMessage();
        }
    }

    // ========== AÇÕES PARA EMPRÉSTIMOS ==========

    @GetMapping("/emprestimos/{id}")
    public String visualizarEmprestimo(@PathVariable Long id, Model model) {
        try {
            // Em uma implementação real, você teria um serviço para buscar empréstimo por ID
            // Por enquanto, vamos redirecionar para a lista
            return "redirect:/emprestimos";
        } catch (Exception e) {
            return "redirect:/emprestimos?error=" + e.getMessage();
        }
    }

    @PostMapping("/emprestimos/{id}/devolver")
    public String devolverEmprestimo(@PathVariable Long id) {
        try {
            emprestimoService.devolverLivro(id);
            return "redirect:/emprestimos?sucesso=Livro devolvido com sucesso";
        } catch (Exception e) {
            return "redirect:/emprestimos?error=" + e.getMessage();
        }
    }
}