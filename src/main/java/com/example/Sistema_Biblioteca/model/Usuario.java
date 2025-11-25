package com.example.Sistema_Biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Matrícula é obrigatória")
    @Column(unique = true, nullable = false)
    private String matricula;

    @Email(message = "Email deve ser válido")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha; // Senha criptografada

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    // REMOVA o CascadeType.ALL para evitar apagar histórico se o usuário for deletado
    @OneToMany(mappedBy = "usuario")
    private List<Emprestimo> emprestimos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Reserva> reservas = new ArrayList<>();

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    // --- CONSTRUTORES ---

    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
    }

    // Construtor usado no Registro (com senha)
    public Usuario(String nome, String matricula, String email, String senha, TipoUsuario tipo) {
        this();
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // --- GETTERS E SETTERS PADRÃO (Necessários para o CrudController) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) { // O CrudController precisa deste método
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) { // O CrudController precisa deste método
        this.matricula = matricula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { // O CrudController precisa deste método
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) { // O CrudController precisa deste método
        this.tipo = tipo;
    }

    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // --- MÉTODOS DE SEGURANÇA (UserDetails) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipo == TipoUsuario.FUNCIONARIO) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}