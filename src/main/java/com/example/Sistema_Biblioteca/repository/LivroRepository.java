package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByIsbn(String isbn);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);

    @Query("SELECT l FROM Livro l WHERE l.exemplaresDisponiveis > 0")
    List<Livro> findLivrosDisponiveis();

    @Query("SELECT l FROM Livro l WHERE l.categoria.nome = :categoria AND l.exemplaresDisponiveis > 0")
    List<Livro> findLivrosDisponiveisPorCategoria(@Param("categoria") String categoria);

    @Query("SELECT l FROM Livro l JOIN l.autores a WHERE a.nome LIKE %:autor%")
    List<Livro> findByAutorNomeContaining(@Param("autor") String autor);

    @Query("SELECT l FROM Livro l WHERE l.anoPublicacao BETWEEN :anoInicio AND :anoFim")
    List<Livro> findByAnoPublicacaoBetween(@Param("anoInicio") Integer anoInicio, @Param("anoFim") Integer anoFim);
}