package com.biblioteca.service;

import com.biblioteca.model.*;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Value("${app.emprestimo.dias-emprestimo:14}")
    private int diasEmprestimo;

    @Value("${app.emprestimo.multa-por-dia:2.0}")
    private double multaPorDia;

    @Value("${app.emprestimo.max-livros-por-usuario:3}")
    private int maxLivrosPorUsuario;

    @Transactional
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Validações
        validarEmprestimo(usuario, livro);

        // Criar empréstimo
        LocalDateTime dataDevolucaoPrevista = LocalDateTime.now().plusDays(diasEmprestimo);
        Emprestimo emprestimo = new Emprestimo(usuario, livro, dataDevolucaoPrevista);

        // Atualizar disponibilidade do livro
        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() - 1);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);
    }

    private void validarEmprestimo(Usuario usuario, Livro livro) {
        // Verificar se há exemplares disponíveis
        if (livro.getExemplaresDisponiveis() <= 0) {
            throw new RuntimeException("Não há exemplares disponíveis deste livro");
        }

        // Verificar limite de empréstimos
        Long emprestimosAtivos = emprestimoRepository.countEmprestimosAtivosByUsuario(usuario);
        if (emprestimosAtivos >= maxLivrosPorUsuario) {
            throw new RuntimeException("Usuário atingiu o limite máximo de empréstimos");
        }

        // Verificar se usuário tem multas pendentes
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository.findEmprestimosAtrasados(LocalDateTime.now());
        boolean temMultaPendente = emprestimosAtrasados.stream()
                .anyMatch(e -> e.getUsuario().equals(usuario) && e.getMulta() != null &&
                        e.getMulta().getStatus() == StatusMulta.PENDENTE);

        if (temMultaPendente) {
            throw new RuntimeException("Usuário possui multas pendentes");
        }
    }

    @Transactional
    public Emprestimo devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RuntimeException("Livro já foi devolvido");
        }

        // Atualizar status e data de devolução
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucaoReal(LocalDateTime.now());

        // Atualizar disponibilidade do livro
        Livro livro = emprestimo.getLivro();
        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() + 1);
        livroRepository.save(livro);

        // Verificar e aplicar multa se houver atraso
        if (emprestimo.getDataDevolucaoReal().isAfter(emprestimo.getDataDevolucaoPrevista())) {
            aplicarMulta(emprestimo);
        }

        return emprestimoRepository.save(emprestimo);
    }

    private void aplicarMulta(Emprestimo emprestimo) {
        long diasAtraso = java.time.Duration.between(
                emprestimo.getDataDevolucaoPrevista(),
                emprestimo.getDataDevolucaoReal()
        ).toDays();

        if (diasAtraso > 0) {
            BigDecimal valorMulta = BigDecimal.valueOf(diasAtraso * multaPorDia);
            Multa multa = new Multa(emprestimo, valorMulta);
            emprestimo.setMulta(multa);
        }
    }

    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.ATIVO);
    }

    public List<Emprestimo> listarEmprestimosAtrasados() {
        return emprestimoRepository.findEmprestimosAtrasados(LocalDateTime.now());
    }

    public List<Emprestimo> listarEmprestimosPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return emprestimoRepository.findByUsuario(usuario);
    }
}