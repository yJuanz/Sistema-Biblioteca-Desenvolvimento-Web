package com.example.Sistema_Biblioteca.dto;

import java.time.LocalDateTime;

public class EmprestimoRequestDTO {
    private Long usuarioId;
    private Long livroId;
    private LocalDateTime dataDevolucaoPrevista;

    // Construtores
    public EmprestimoRequestDTO() {}

    public EmprestimoRequestDTO(Long usuarioId, Long livroId, LocalDateTime dataDevolucaoPrevista) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    // Getters e Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }

    public LocalDateTime getDataDevolucaoPrevista() { return dataDevolucaoPrevista; }
    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }
}