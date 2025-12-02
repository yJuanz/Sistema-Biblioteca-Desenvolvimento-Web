package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.*;
import com.example.Sistema_Biblioteca.repository.LivroRepository;
import com.example.Sistema_Biblioteca.repository.ReservaRepository;
import com.example.Sistema_Biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Transactional
    public Reserva realizarReserva(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Verificar se usuário já tem reserva ativa para este livro
        Optional<Reserva> reservaExistente = reservaRepository.findByUsuarioAndLivroAndStatus(
                usuario, livro, StatusReserva.ATIVA
        );

        if (reservaExistente.isPresent()) {
            throw new RuntimeException("Usuário já possui uma reserva ativa para este livro.");
        }

        // Criar a reserva (validade de 7 dias)
        LocalDateTime dataExpiracao = LocalDateTime.now().plusDays(7);
        Reserva reserva = new Reserva(usuario, livro, dataExpiracao);
        reserva.setStatus(StatusReserva.ATIVA);

        return reservaRepository.save(reserva);
    }
}