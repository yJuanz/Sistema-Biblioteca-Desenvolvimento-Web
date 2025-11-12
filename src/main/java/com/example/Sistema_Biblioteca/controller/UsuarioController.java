package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.model.TipoUsuario;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Usuario> buscarPorMatricula(@PathVariable String matricula) {
        Optional<Usuario> usuario = usuarioService.buscarPorMatricula(matricula);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> buscarPorEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<Usuario>> buscarPorNome(@RequestParam String nome) {
        List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Usuario>> buscarPorTipo(@PathVariable TipoUsuario tipo) {
        List<Usuario> usuarios = usuarioService.buscarPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioCriado = usuarioService.criar(usuario);
            return ResponseEntity.ok(usuarioCriado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/pode-emprestar")
    public ResponseEntity<Boolean> usuarioPodeEmprestar(@PathVariable Long id) {
        try {
            boolean podeEmprestar = usuarioService.usuarioPodeEmprestar(id);
            return ResponseEntity.ok(podeEmprestar);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/{id}/emprestimos-ativos")
    public ResponseEntity<Long> contarEmprestimosAtivos(@PathVariable Long id) {
        try {
            Long count = usuarioService.contarEmprestimosAtivos(id);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

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