package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.model.TipoUsuario;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para o gerenciamento de Usuários.
 * Centraliza todas as operações relacionadas a alunos, professores e funcionários.
 * Fornece endpoints para o Front-end realizar cadastros, buscas e validações.
 */
@RestController
@RequestMapping("/api/usuarios") // Base URL: http://localhost:8080/api/usuarios
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários cadastrados no banco.
     * Usado para popular a tabela de administração de usuários.
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca um usuário pelo ID primário.
     * Retorna 404 (Not Found) caso o usuário não exista.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca específica por Matrícula (Útil para o sistema de empréstimo rápido).
     */
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Usuario> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Usuario> usuario = usuarioService.buscarPorMatricula(matricula);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca por Email (Útil para validação de login ou recuperação de conta).
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca parcial por nome (LIKE).
     * Permite filtrar a lista de usuários enquanto se digita.
     */
    @GetMapping("/busca")
    public ResponseEntity<List<Usuario>> buscarPorNome(@RequestParam String nome) {
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Filtra usuários pelo tipo (ALUNO, PROFESSOR, FUNCIONARIO).
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Usuario>> buscarPorTipo(@PathVariable TipoUsuario tipo) {
        List<Usuario> usuarios = usuarioService.buscarPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Cria um novo usuário.
     * Encapsula a chamada em um try-catch para retornar erro 400 se houver
     * violação de regras (ex: email ou matrícula duplicados).
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioCriado = usuarioService.criar(usuario);
            return ResponseEntity.ok(usuarioCriado);
        } catch (RuntimeException e) {
            // Retorna o erro exato do Service para o Front-end exibir
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Atualiza os dados de um usuário existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove um usuário.
     * O Service impede a exclusão se o usuário tiver livros pendentes.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- ENDPOINTS DE REGRA DE NEGÓCIO ---

    /**
     * Verifica se o usuário está apto a realizar novos empréstimos.
     * Valida limites de livros e existência de multas.
     */
    @GetMapping("/{id}/pode-emprestar")
    public ResponseEntity<Boolean> usuarioPodeEmprestar(@PathVariable Long id) {
        try {
            boolean podeEmprestar = usuarioService.usuarioPodeEmprestar(id);
            return ResponseEntity.ok(podeEmprestar);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    /**
     * Retorna quantos livros o usuário tem com ele no momento.
     */
    @GetMapping("/{id}/emprestimos-ativos")
    public ResponseEntity<Long> contarEmprestimosAtivos(@PathVariable Long id) {
        try {
            Long count = usuarioService.contarEmprestimosAtivos(id);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- ENDPOINTS DE VALIDAÇÃO (AJAX) ---

    /**
     * Verifica se um email já existe no banco.
     * Usado pelo formulário de cadastro para validação em tempo real.
     */
    @GetMapping("/verificar-email")
    public ResponseEntity<Boolean> verificarEmailExistente(@RequestParam String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario.isPresent());
    }

    @GetMapping("/verificar-matricula")
    public ResponseEntity<Boolean> verificarMatriculaExistente(@RequestParam String matricula) {
        Optional<Usuario> usuario = usuarioService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(usuario.isPresent());
    }
}