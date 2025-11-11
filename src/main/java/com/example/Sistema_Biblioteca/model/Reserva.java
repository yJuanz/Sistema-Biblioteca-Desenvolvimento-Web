package com.example.Sistema_Biblioteca.model;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    // Chave Primária Composta
    @EmbeddedId
    private ReservaId id;

    // Relação Muitos-para-Um com Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relação Muitos-para-Um com Livro
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("livroId")
    @JoinColumn(name = "livro_id")
    private Livro livro;

    // REMOVA esta linha duplicada ↓
    // @Column(name = "data_reserva", nullable = false)
    // private LocalDateTime dataReserva;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status;

    // Construtores
    public Reserva() {
        // A dataReserva agora está apenas no ReservaId
        this.status = StatusReserva.ATIVA;
    }

    public Reserva(Usuario usuario, Livro livro, LocalDateTime dataExpiracao) {
        this();
        this.id = new ReservaId(usuario.getId(), livro.getId(), LocalDateTime.now());
        this.usuario = usuario;
        this.livro = livro;
        this.dataExpiracao = dataExpiracao;
    }

    // Getters e Setters
    public ReservaId getId() { return id; }
    public void setId(ReservaId id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    // Getter para dataReserva que vem do ID
    public LocalDateTime getDataReserva() {
        return id != null ? id.getDataReserva() : null;
    }

    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }

    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
}

// Classe para Chave Primária Composta
@Embeddable
class ReservaId implements Serializable {
    private Long usuarioId;
    private Long livroId;

    @Column(name = "data_reserva") // Apenas aqui temos a coluna data_reserva
    private LocalDateTime dataReserva;

    public ReservaId() {}

    public ReservaId(Long usuarioId, Long livroId, LocalDateTime dataReserva) {
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataReserva = dataReserva;
    }

    // Getters e Setters
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLivroId() { return livroId; }
    public void setLivroId(Long livroId) { this.livroId = livroId; }

    public LocalDateTime getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDateTime dataReserva) { this.dataReserva = dataReserva; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaId)) return false;
        ReservaId that = (ReservaId) o;
        return usuarioId.equals(that.usuarioId) &&
                livroId.equals(that.livroId) &&
                dataReserva.equals(that.dataReserva);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(usuarioId, livroId, dataReserva);
    }
}

