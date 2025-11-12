package com.example.Sistema_Biblioteca.repository;

import com.example.Sistema_Biblioteca.model.Reserva;
import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuario(Usuario usuario);
    List<Reserva> findByLivro(Livro livro);

    @Query("SELECT r FROM Reserva r WHERE r.status = 'ATIVA' AND r.dataExpiracao < :dataAtual")
    List<Reserva> findReservasExpiradas(@Param("dataAtual") LocalDateTime dataAtual);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.livro = :livro AND r.status = 'ATIVA'")
    Long countReservasAtivasPorLivro(@Param("livro") Livro livro);

    Optional<Reserva> findByUsuarioAndLivroAndStatus(Usuario usuario, Livro livro, com.example.Sistema_Biblioteca.model.StatusReserva status);
}