package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.LivroAutor;
import com.example.Sistema_Biblioteca.model.LivroAutorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// A chave primária é a classe LivroAutorId
@Repository
public interface LivroAutorRepository extends JpaRepository<LivroAutor, LivroAutorId> {
}