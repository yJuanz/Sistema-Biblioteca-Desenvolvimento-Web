package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.Livro;
import com.example.Sistema_Biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @GetMapping
    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        return livro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponiveis")
    public List<Livro> listarDisponiveis() {
        return livroRepository.findLivrosDisponiveis();
    }

    @GetMapping("/busca")
    public List<Livro> buscarLivros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer anoInicio,
            @RequestParam(required = false) Integer anoFim) {

        if (titulo != null) {
            return livroRepository.findByTituloContainingIgnoreCase(titulo);
        } else if (autor != null) {
            return livroRepository.findByAutorNomeContaining(autor);
        } else if (categoria != null) {
            return livroRepository.findLivrosDisponiveisPorCategoria(categoria);
        } else if (anoInicio != null && anoFim != null) {
            return livroRepository.findByAnoPublicacaoBetween(anoInicio, anoFim);
        }

        return livroRepository.findAll();
    }
}