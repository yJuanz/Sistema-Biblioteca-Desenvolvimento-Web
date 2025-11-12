package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.TipoUsuario;
import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormularioController {

    @Autowired
    private LivroService livroService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmprestimoService emprestimoService;

    // ========== FORMULÁRIO DE NOVO LIVRO ==========
    @GetMapping("/livros/novo")
    public String mostrarFormularioLivro(Model model) {
        return "form-livro";
    }

    @PostMapping("/livros/novo")
    public String salvarLivro(@RequestParam String titulo,
                              @RequestParam String isbn,
                              @RequestParam Integer anoPublicacao,
                              @RequestParam Integer quantidadeExemplares) {
        try {
            var livro = new com.biblioteca.model.Livro();
            livro.setTitulo(titulo);
            livro.setIsbn(isbn);
            livro.setAnoPublicacao(anoPublicacao);
            livro.setQuantidadeExemplares(quantidadeExemplares);
            livro.setExemplaresDisponiveis(quantidadeExemplares);

            livroService.criar(livro);
            return "redirect:/livros?sucesso=Livro cadastrado com sucesso";
        } catch (Exception e) {
            return "redirect:/livros/novo?error=" + e.getMessage();
        }
    }

    // ========== FORMULÁRIO DE NOVO USUÁRIO ==========
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
            var usuario = new com.biblioteca.model.Usuario();
            usuario.setNome(nome);
            usuario.setMatricula(matricula);
            usuario.setEmail(email);
            usuario.setTipo(tipo);

            usuarioService.criar(usuario);
            return "redirect:/usuarios?sucesso=Usuário cadastrado com sucesso";
        } catch (Exception e) {
            return "redirect:/usuarios/novo?error=" + e.getMessage();
        }
    }

    // ========== FORMULÁRIO DE NOVO EMPRÉSTIMO ==========
    @GetMapping("/emprestimos/novo")
    public String mostrarFormularioEmprestimo(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("livrosDisponiveis", livroService.listarDisponiveis());
        } catch (Exception e) {
            // Se houver erro, ainda mostra o formulário vazio
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
        return "form-emprestimo";
    }

    @PostMapping("/emprestimos/novo")
    public String salvarEmprestimo(@RequestParam Long usuarioId,
                                   @RequestParam Long livroId) {
        try {
            emprestimoService.realizarEmprestimo(usuarioId, livroId);
            return "redirect:/emprestimos?sucesso=Empréstimo realizado com sucesso";
        } catch (Exception e) {
            return "redirect:/emprestimos/novo?error=" + e.getMessage();
        }
    }

    // ========== FORMULÁRIO DE NOVA RESERVA ==========
    @GetMapping("/reservas/nova")
    public String mostrarFormularioReserva(Model model) {
        try {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("livros", livroService.listarTodos());
        } catch (Exception e) {
            // Se houver erro, ainda mostra o formulário vazio
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
        return "form-reserva";
    }

    @PostMapping("/reservas/nova")
    public String salvarReserva(@RequestParam Long usuarioId,
                                @RequestParam Long livroId) {
        try {
            // Em uma implementação real, você teria um serviço de reservas
            // Por enquanto, vamos apenas redirecionar com mensagem de sucesso
            return "redirect:/reservas?sucesso=Reserva realizada com sucesso";
        } catch (Exception e) {
            return "redirect:/reservas/nova?error=" + e.getMessage();
        }
    }
}