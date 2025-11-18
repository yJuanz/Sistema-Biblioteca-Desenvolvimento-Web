package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// A chave é um Long (o ID do Emprestimo)
@Repository
public interface MultaRepository extends JpaRepository<Multa, Long> {
}