package com.example.Sistema_Biblioteca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livro_autor")
public class LivroAutor {

    // 1. Usa a classe de chave composta
    @EmbeddedId
    private LivroAutorId id;

    // 2. Mapeia a relação para Livro
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("livroId") // <-- Diz ao JPA que este campo faz parte da PK
    @JoinColumn(name = "livro_id")
    private Livro livro;

    // 3. Mapeia a relação para Autor
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("autorId") // <-- Diz ao JPA que este campo faz parte da PK
    @JoinColumn(name = "autor_id")
    private Autor autor;

    // Construtores
    public LivroAutor() {}

    public LivroAutor(LivroAutorId id, Livro livro, Autor autor) {
        this.id = id;
        this.livro = livro;
        this.autor = autor;
    }

    public LivroAutorId getId() {
        return id;
    }

    public void setId(LivroAutorId id) {
        this.id = id;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }
    
}