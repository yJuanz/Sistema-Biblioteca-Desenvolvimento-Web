package com.example.Sistema_Biblioteca.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

// 1. Marca como "embutível"
@Embeddable
public class LivroAutorId implements Serializable {

    // 2. Colunas que formam a chave composta
    private Long livroId;
    private Long autorId;

    // Construtores, Getters, Setters
    public LivroAutorId() {}

    public LivroAutorId(Long livroId, Long autorId) {
        this.livroId = livroId;
        this.autorId = autorId;
    }

    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public Long getAutorId() {
        return autorId;
    }

    public void setAutorId(Long autorId) {
        this.autorId = autorId;
    }

    // 3. OBRIGATÓRIO: equals() e hashCode() para chaves compostas
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivroAutorId that = (LivroAutorId) o;
        return Objects.equals(livroId, that.livroId) &&
               Objects.equals(autorId, that.autorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(livroId, autorId);
    }
}