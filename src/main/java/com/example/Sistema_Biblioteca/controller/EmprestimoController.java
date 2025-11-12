package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.dto.EmprestimoRequestDTO;
import com.example.Sistema_Biblioteca.model.Emprestimo;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping("/realizar")
    public ResponseEntity<?> realizarEmprestimo(@RequestBody EmprestimoRequestDTO request) {
        try {
            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(
                    request.getUsuarioId(),
                    request.getLivroId()
            );
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolverLivro(@PathVariable Long id) {
        try {
            Emprestimo emprestimo = emprestimoService.devolverLivro(id);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosAtivos();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosAtrasados() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosAtrasados();
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosPorUsuario(@PathVariable Long usuarioId) {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosPorUsuario(usuarioId);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable Long id) {
        try {
            // Implementar busca por ID no service se necessário
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}