package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST dedicado a análises e estatísticas (Data Analytics).
 * DIFERENCIAL: Este controller não manipula entidades diretamente (CRUD),
 * mas sim agrega dados de várias fontes para alimentar os Dashboards.
 */
@RestController
@RequestMapping("/api/relatorios") // Endpoint base: http://localhost:8080/api/relatorios
public class RelatorioController {

    // Serviço especializado em compilar dados de Empréstimos, Livros e Usuários
    @Autowired
    private RelatorioService relatorioService;

    /**
     * Endpoint para o Dashboard Principal (Gráficos Gerais).
     * Retorna um JSON flexível (Map<String, Object>) contendo métricas consolidadas.
     * Isso permite que o Front-end desenhe gráficos sem precisar fazer cálculos matemáticos.
     * * Retorno esperado:
     * {
     * "totalLivros": 150,
     * "livrosMaisEmprestados": [...],
     * "taxaInadimplencia": "5%"
     * }
     */
    @GetMapping("/geral")
    public ResponseEntity<Map<String, Object>> relatorioGeral() {
        // O Service faz o trabalho pesado de consultar vários repositórios e calcular totais
        Map<String, Object> relatorio = relatorioService.gerarRelatorioGeral();
        
        // Retorna 200 OK com o JSON gerado
        return ResponseEntity.ok(relatorio);
    }

    /**
     * Endpoint para análise individual de um usuário (Perfil do Leitor).
     * Útil para exibir o histórico, reputação ou pendências de uma pessoa específica.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> relatorioUsuario(@PathVariable Long usuarioId) {
        Map<String, Object> relatorio = relatorioService.gerarRelatorioUsuario(usuarioId);
        return ResponseEntity.ok(relatorio);
    }
}