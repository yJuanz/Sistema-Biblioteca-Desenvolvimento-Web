package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.Livro;
import com.example.Sistema_Biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para consulta de livros.
 * Diferente do CrudController, este endpoint serve dados em JSON, ideal para:
 * 1. Integração com aplicativos móveis.
 * 2. Front-ends modernos (React, Vue, Angular).
 * 3. Consultas assíncronas (AJAX) dentro do próprio sistema.
 */
@RestController // Define que a resposta será o corpo do objeto (JSON) e não uma view HTML
@RequestMapping("/api/livros") // Prefixo para todos os endpoints: http://localhost:8080/api/livros
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    /**
     * Lista todo o acervo da biblioteca.
     * Método: GET /api/livros
     */
    @GetMapping
    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    /**
     * Busca um livro específico pelo ID.
     * Tratamento de erro: Retorna 404 (Not Found) se o ID não existir.
     * Método: GET /api/livros/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
        Optional<Livro> livro = livroRepository.findById(id);
        
        // Uso de programação funcional (map/orElse) para resposta limpa
        return livro.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Filtra apenas os livros que possuem exemplares no estoque.
     * Útil para usuários que querem saber o que podem pegar agora.
     * Método: GET /api/livros/disponiveis
     */
    @GetMapping("/disponiveis")
    public List<Livro> listarDisponiveis() {
        return livroRepository.findLivrosDisponiveis();
    }

    /**
     * Sistema de Busca Avançada.
     * Utiliza parâmetros opcionais na URL (Query Params) para flexibilizar a pesquisa.
     * Exemplo de uso: /api/livros/busca?titulo=Harry
     * Exemplo de uso: /api/livros/busca?autor=Tolkien
     */
    @GetMapping("/busca")
    public List<Livro> buscarLivros(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer anoInicio,
            @RequestParam(required = false) Integer anoFim) {

        // Lógica de decisão: Verifica qual filtro foi enviado e chama a query específica
        if (titulo != null) {
            // Busca parcial (LIKE) ignorando maiúsculas/minúsculas
            return livroRepository.findByTituloContainingIgnoreCase(titulo);
        } else if (autor != null) {
            // Busca livros pelo nome do autor (Join automático no repositório)
            return livroRepository.findByAutorNomeContaining(autor);
        } else if (categoria != null) {
            // Busca apenas livros disponíveis de uma categoria específica
            return livroRepository.findLivrosDisponiveisPorCategoria(categoria);
        } else if (anoInicio != null && anoFim != null) {
            // Busca por intervalo de tempo
            return livroRepository.findByAnoPublicacaoBetween(anoInicio, anoFim);
        }

        // Se nenhum filtro for passado, retorna tudo
        return livroRepository.findAll();
    }
}