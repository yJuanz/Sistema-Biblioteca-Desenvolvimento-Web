package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.Emprestimo;
import com.example.Sistema_Biblioteca.model.Livro;
import com.example.Sistema_Biblioteca.repository.EmprestimoRepository;
import com.example.Sistema_Biblioteca.repository.LivroRepository;
import com.example.Sistema_Biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Map<String, Object> gerarRelatorioGeral() {
        Map<String, Object> relatorio = new HashMap<>();

        // Estatísticas básicas
        relatorio.put("totalLivros", livroRepository.count());
        relatorio.put("totalUsuarios", usuarioRepository.count());

        // Livros mais emprestados
        List<Livro> todosLivros = livroRepository.findAll();
        List<Map<String, Object>> livrosMaisEmprestados = todosLivros.stream()
                .filter(livro -> !livro.getEmprestimos().isEmpty())
                .sorted((l1, l2) -> Integer.compare(l2.getEmprestimos().size(), l1.getEmprestimos().size()))
                .limit(10)
                .map(livro -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("titulo", livro.getTitulo());
                    info.put("totalEmprestimos", livro.getEmprestimos().size());
                    info.put("disponiveis", livro.getExemplaresDisponiveis());
                    return info;
                })
                .collect(Collectors.toList());

        relatorio.put("livrosMaisEmprestados", livrosMaisEmprestados);

        // Empréstimos do mês
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fimMes = LocalDateTime.now().withDayOfMonth(1).plusMonths(1).minusSeconds(1);

        List<Emprestimo> emprestimosMes = emprestimoRepository.findEmprestimosPorPeriodo(inicioMes, fimMes);
        relatorio.put("emprestimosEsteMes", emprestimosMes.size());

        // Empréstimos atrasados
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findEmprestimosAtrasados(LocalDateTime.now());
        relatorio.put("emprestimosAtrasados", emprestimosAtrasados.size());

        return relatorio;
    }

    public Map<String, Object> gerarRelatorioUsuario(Long usuarioId) {
        Map<String, Object> relatorio = new HashMap<>();

        // Implementar relatório específico do usuário
        // Histórico de empréstimos, multas, etc.

        return relatorio;
    }
}