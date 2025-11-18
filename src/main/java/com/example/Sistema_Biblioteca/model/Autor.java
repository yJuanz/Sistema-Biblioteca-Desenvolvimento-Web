package com.example.Sistema_Biblioteca.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate; // Supondo que você use LocalDate para dataNascimento
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "autor")
public class Autor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String nacionalidade;
    
    private LocalDate dataNascimento; // Mantive este campo caso você o tenha

    /**
     * Relação 1:N com a entidade de junção LivroAutor.
     * Esta é a ponta "N" da relação N:N entre Livro e Autor.
     * mappedBy = "autor" indica que a entidade LivroAutor gerencia a relação.
     */
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<LivroAutor> livroAutores = new HashSet<>();

    // --- Construtores ---
    
    public Autor() {}

    public Autor(String nome, String nacionalidade, LocalDate dataNascimento) {
        this.nome = nome;
        this.nacionalidade = nacionalidade;
        this.dataNascimento = dataNascimento;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Set<LivroAutor> getLivroAutores() {
        return livroAutores;
    }

    public void setLivroAutores(Set<LivroAutor> livroAutores) {
        this.livroAutores = livroAutores;
    }
    
    // --- equals() e hashCode() ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Autor autor = (Autor) o;
        return Objects.equals(id, autor.id) &&
               Objects.equals(nome, autor.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }
}