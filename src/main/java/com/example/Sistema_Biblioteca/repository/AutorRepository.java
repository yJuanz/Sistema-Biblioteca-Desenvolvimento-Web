package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);

    @Query("SELECT a FROM Autor a WHERE a.nome LIKE %:nome%")
    List<Autor> findByNomeContaining(@Param("nome") String nome);

    List<Autor> findByNacionalidade(String nacionalidade);
}