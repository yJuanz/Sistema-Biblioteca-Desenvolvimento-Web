package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.model.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMatricula(String matricula);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByTipo(TipoUsuario tipo);

    @Query("SELECT u FROM Usuario u WHERE u.nome LIKE %:nome%")
    List<Usuario> findByNomeContaining(@Param("nome") String nome);

    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario = :usuario AND e.status = 'ATIVO'")
    Long countEmprestimosAtivosPorUsuario(@Param("usuario") Usuario usuario);
}