package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Editora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditoraRepository extends JpaRepository<Editora, Long> {
    Optional<Editora> findByNome(String nome);
    boolean existsByNome(String nome);
}