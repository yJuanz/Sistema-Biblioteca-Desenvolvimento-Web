package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Emprestimo;
import com.example.Sistema_Biblioteca.model.StatusEmprestimo;
import com.example.Sistema_Biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuario(Usuario usuario);
    List<Emprestimo> findByStatus(StatusEmprestimo status);

    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoPrevista < :dataAtual AND e.status = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtrasados(@Param("dataAtual") LocalDateTime dataAtual);

    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario = :usuario AND e.status = 'ATIVO'")
    Long countEmprestimosAtivosByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT e FROM Emprestimo e WHERE e.livro.id = :livroId AND e.status = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtivosPorLivro(@Param("livroId") Long livroId);

    @Query("SELECT e FROM Emprestimo e WHERE e.dataEmprestimo BETWEEN :inicio AND :fim")
    List<Emprestimo> findEmprestimosPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}