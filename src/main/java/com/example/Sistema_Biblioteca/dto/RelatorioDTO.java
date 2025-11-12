package com.example.Sistema_Biblioteca.dto;

import java.util.List;
import java.util.Map;

public class RelatorioDTO {
    private Long totalLivros;
    private Long totalUsuarios;
    private Long emprestimosAtivos;
    private Long emprestimosAtrasados;
    private Long livrosDisponiveis;
    private List<Map<String, Object>> livrosMaisEmprestados;
    private Map<String, Long> estatisticasPorCategoria;
    private Map<String, Long> estatisticasPorTipoUsuario;

    // Construtores
    public RelatorioDTO() {}

    // Getters e Setters
    public Long getTotalLivros() { return totalLivros; }
    public void setTotalLivros(Long totalLivros) { this.totalLivros = totalLivros; }

    public Long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(Long totalUsuarios) { this.totalUsuarios = totalUsuarios; }

    public Long getEmprestimosAtivos() { return emprestimosAtivos; }
    public void setEmprestimosAtivos(Long emprestimosAtivos) { this.emprestimosAtivos = emprestimosAtivos; }

    public Long getEmprestimosAtrasados() { return emprestimosAtrasados; }
    public void setEmprestimosAtrasados(Long emprestimosAtrasados) { this.emprestimosAtrasados = emprestimosAtrasados; }

    public Long getLivrosDisponiveis() { return livrosDisponiveis; }
    public void setLivrosDisponiveis(Long livrosDisponiveis) { this.livrosDisponiveis = livrosDisponiveis; }

    public List<Map<String, Object>> getLivrosMaisEmprestados() { return livrosMaisEmprestados; }
    public void setLivrosMaisEmprestados(List<Map<String, Object>> livrosMaisEmprestados) {
        this.livrosMaisEmprestados = livrosMaisEmprestados;
    }

    public Map<String, Long> getEstatisticasPorCategoria() { return estatisticasPorCategoria; }
    public void setEstatisticasPorCategoria(Map<String, Long> estatisticasPorCategoria) {
        this.estatisticasPorCategoria = estatisticasPorCategoria;
    }

    public Map<String, Long> getEstatisticasPorTipoUsuario() { return estatisticasPorTipoUsuario; }
    public void setEstatisticasPorTipoUsuario(Map<String, Long> estatisticasPorTipoUsuario) {
        this.estatisticasPorTipoUsuario = estatisticasPorTipoUsuario;
    }
}