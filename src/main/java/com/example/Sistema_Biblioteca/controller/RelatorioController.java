package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/geral")
    public ResponseEntity<Map<String, Object>> relatorioGeral() {
        Map<String, Object> relatorio = relatorioService.gerarRelatorioGeral();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> relatorioUsuario(@PathVariable Long usuarioId) {
        Map<String, Object> relatorio = relatorioService.gerarRelatorioUsuario(usuarioId);
        return ResponseEntity.ok(relatorio);
    }
}