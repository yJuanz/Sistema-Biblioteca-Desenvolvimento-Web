package com.example.Sistema_Biblioteca.dto;

import java.time.LocalDateTime;

public class ReservaDTO {
    private Long usuarioId;
    private Long livroId;
    private LocalDateTime dataExpiracao;

    // Construtores
    public ReservaDTO() {}

    public ReservaDTO(Long usuarioId, Long livroId, LocalDateTime dataExpiracao) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataExpiracao = dataExpiracao;
    }

    // Getters e Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}