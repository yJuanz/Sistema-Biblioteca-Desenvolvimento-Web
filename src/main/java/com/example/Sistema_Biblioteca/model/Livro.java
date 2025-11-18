package com.example.Sistema_Biblioteca.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "livro")
public class Livro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(name = "quantidade_exemplares", nullable = false)
    private Integer quantidadeExemplares;

    @Column(name = "exemplares_disponiveis", nullable = false)
    private Integer exemplaresDisponiveis;

    // --- Relações ---

    /**
     * Relação 1:N com a entidade de junção LivroAutor.
     * Esta é a ponta "N" da relação N:N entre Livro e Autor.
     * mappedBy = "livro" indica que a entidade LivroAutor gerencia a relação.
     */
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<LivroAutor> livroAutores = new HashSet<>();

    // Relação N:1 com Categoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Relação N:1 com Editora
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "editora_id")
    private Editora editora;
    
    // Relação 1:N com Emprestimo
    // REMOVIDO o "cascade" e "orphanRemoval" para proteger o histórico
    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private Set<Emprestimo> emprestimos = new HashSet<>();

    // Relação 1:N com Reserva
    // REMOVIDO o "cascade" e "orphanRemoval"
    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private Set<Reserva> reservas = new HashSet<>();


    // --- Construtores ---
    
    public Livro() {}

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public Integer getQuantidadeExemplares() {
        return quantidadeExemplares;
    }

    public void setQuantidadeExemplares(Integer quantidadeExemplares) {
        this.quantidadeExemplares = quantidadeExemplares;
    }

    public Integer getExemplaresDisponiveis() {
        return exemplaresDisponiveis;
    }

    public void setExemplaresDisponiveis(Integer exemplaresDisponiveis) {
        this.exemplaresDisponiveis = exemplaresDisponiveis;
    }

    public Set<LivroAutor> getLivroAutores() {
        return livroAutores;
    }

    public void setLivroAutores(Set<LivroAutor> livroAutores) {
        this.livroAutores = livroAutores;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Editora getEditora() {
        return editora;
    }

    public void setEditora(Editora editora) {
        this.editora = editora;
    }

    public Set<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(Set<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public Set<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(Set<Reserva> reservas) {
        this.reservas = reservas;
    }
    
    // --- equals() e hashCode() ---
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return Objects.equals(id, livro.id) &&
               Objects.equals(isbn, livro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn);
    }
}