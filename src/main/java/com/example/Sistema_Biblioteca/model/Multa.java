package com.example.Sistema_Biblioteca.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "multas")
public class Multa {

    // Chave Estrangeira como Chave Primária
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "emprestimo_id")
    private Emprestimo emprestimo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_cobranca", nullable = false)
    private LocalDateTime dataCobranca;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMulta status;

    // Construtores
    public Multa() {
        this.dataCobranca = LocalDateTime.now();
        this.status = StatusMulta.PENDENTE;
    }

    public Multa(Emprestimo emprestimo, BigDecimal valor) {
        this();
        this.emprestimo = emprestimo;
        this.valor = valor;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Emprestimo getEmprestimo() { return emprestimo; }
    public void setEmprestimo(Emprestimo emprestimo) { this.emprestimo = emprestimo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataCobranca() { return dataCobranca; }
    public void setDataCobranca(LocalDateTime dataCobranca) { this.dataCobranca = dataCobranca; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public StatusMulta getStatus() { return status; }
    public void setStatus(StatusMulta status) { this.status = status; }
}

