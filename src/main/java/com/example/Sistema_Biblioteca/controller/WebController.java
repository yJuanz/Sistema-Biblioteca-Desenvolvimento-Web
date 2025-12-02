package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.*;
import com.example.Sistema_Biblioteca.repository.MultaRepository;
import com.example.Sistema_Biblioteca.repository.ReservaRepository;
import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller responsável pela renderização das páginas principais do sistema (View).
 * Diferente do CrudController (focado em ação), este controller foca em exibir dados
 * agregados, dashboards e listas completas para o usuário.
 */
@Controller
public class WebController {

    // Serviços injetados para buscar os dados de negócio
    @Autowired private LivroService livroService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private EmprestimoService emprestimoService;

    // Repositórios injetados para buscas específicas de relatórios
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private MultaRepository multaRepository;

    /**
     * Página Inicial (Dashboard Principal).
     * Exibe um resumo geral do sistema: Total de livros, usuários, empréstimos ativos, etc.
     */
    @GetMapping("/")
    public String home(Model model) {
        // 1. Busca todos os dados necessários
        List<Livro> livros = livroService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> emprestimosAtrasados = emprestimoService.listarEmprestimosAtrasados();

        // 2. Lógica de "Livros Populares": Ordena os livros pela quantidade de empréstimos
        List<Livro> livrosPopulares = livros.stream()
                .sorted((l1, l2) -> Integer.compare(
                        l2.getEmprestimos() != null ? l2.getEmprestimos().size() : 0,
                        l1.getEmprestimos() != null ? l1.getEmprestimos().size() : 0
                ))
                .limit(5) // Pega apenas o Top 5
                .collect(Collectors.toList());

        // 3. Lógica de "Últimos Empréstimos": Ordena por data decrescente
        List<Emprestimo> ultimosEmprestimos = emprestimosAtivos.stream()
                .sorted(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // 4. Envia tudo para o HTML (Thymeleaf)
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("emprestimosAtivos", emprestimosAtivos.size());
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados.size());
        model.addAttribute("livrosPopulares", livrosPopulares);
        model.addAttribute("ultimosEmprestimos", ultimosEmprestimos);

        return "index"; // Renderiza o arquivo index.html
    }

    /**
     * Página de Gestão de Livros.
     * Exibe a lista completa e calcula estatísticas de estoque.
     */
    @GetMapping("/livros")
    public String livros(Model model) {
        List<Livro> livros = livroService.listarTodos();
        
        // Uso de Streams do Java 8 para calcular métricas rápidas em memória
        long livrosDisponiveis = livros.stream().filter(l -> l.getExemplaresDisponiveis() > 0).count();
        long livrosIndisponiveis = livros.stream().filter(l -> l.getExemplaresDisponiveis() == 0).count();
        
        long categoriasCount = livros.stream()
                .map(Livro::getCategoria)
                .filter(Objects::nonNull)
                .map(Categoria::getNome)
                .distinct()
                .count();

        model.addAttribute("livros", livros);
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("livrosDisponiveis", livrosDisponiveis);
        model.addAttribute("livrosIndisponiveis", livrosIndisponiveis);
        model.addAttribute("categoriasCount", categoriasCount);

        return "livros";
    }

    /**
     * Página de Gestão de Usuários.
     * Exibe usuários e conta quantos são Alunos, Professores ou Funcionários.
     */
    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        long alunosCount = usuarios.stream().filter(u -> u.getTipo().name().equals("ALUNO")).count();
        long professoresCount = usuarios.stream().filter(u -> u.getTipo().name().equals("PROFESSOR")).count();
        long funcionariosCount = usuarios.stream().filter(u -> u.getTipo().name().equals("FUNCIONARIO")).count();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("alunosCount", alunosCount);
        model.addAttribute("professoresCount", professoresCount);
        model.addAttribute("funcionariosCount", funcionariosCount);

        return "usuarios";
    }

    /**
     * Página de Gestão de Empréstimos.
     * Exibe listas separadas de Ativos e Atrasados e calcula o total financeiro de multas pendentes.
     */
    @GetMapping("/emprestimos")
    public String emprestimos(Model model) {
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> emprestimosAtrasados = emprestimoService.listarEmprestimosAtrasados();

        // Cálculo Financeiro: Soma o valor de todas as multas com status PENDENTE
        double totalMultas = emprestimosAtrasados.stream()
                .filter(e -> e.getMulta() != null && e.getMulta().getStatus().name().equals("PENDENTE"))
                .mapToDouble(e -> e.getMulta().getValor().doubleValue())
                .sum();

        model.addAttribute("emprestimosAtivos", emprestimosAtivos);
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados);
        model.addAttribute("emprestimosAtivosCount", emprestimosAtivos.size());
        model.addAttribute("emprestimosAtrasadosCount", emprestimosAtrasados.size());
        model.addAttribute("totalEmprestimos", emprestimosAtivos.size() + emprestimosAtrasados.size());
        model.addAttribute("totalMultas", String.format("R$ %.2f", totalMultas));

        return "emprestimos";
    }

    /**
     * Página de Gestão de Reservas.
     * Separa visualmente as reservas ativas das expiradas.
     */
    @GetMapping("/reservas")
    public String reservas(Model model) {
        // Busca reservas ativas ordenadas por data de expiração (quem expira primeiro aparece no topo)
        List<Reserva> reservasAtivas = reservaRepository.findByStatusOrderByDataExpiracaoAsc(StatusReserva.ATIVA);
        
        // Busca reservas que já passaram do prazo
        List<Reserva> reservasExpiradas = reservaRepository.findReservasExpiradas(LocalDateTime.now());
        
        model.addAttribute("reservasAtivas", reservasAtivas);
        model.addAttribute("reservasExpiradas", reservasExpiradas);
        model.addAttribute("totalReservas", reservaRepository.count());
        model.addAttribute("reservasAtivasCount", reservasAtivas.size());
        model.addAttribute("reservasExpiradasCount", reservasExpiradas.size());
        model.addAttribute("livrosMaisReservadosCount", 0); 

        // Carrega listas para popular os selects do formulário modal de "Nova Reserva"
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("livros", livroService.listarTodos()); 

        return "reservas";
    }

    /**
     * Página de Relatórios e Análises.
     * Consolida dados de todas as áreas para gerar tabelas e gráficos de performance.
     */
    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        List<Livro> livros = livroService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Emprestimo> todosEmprestimos = emprestimoService.listarTodos(); 
        List<Multa> multas = multaRepository.findAll();

        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("totalEmprestimos", todosEmprestimos.size());

        // KPI: Taxa de Utilização do Acervo
        // Calcula a porcentagem de livros que estão emprestados no momento
        double taxaUtilizacao = livros.isEmpty() ? 0 :
                (double) livros.stream().filter(l -> l.getExemplaresDisponiveis() < l.getQuantidadeExemplares()).count() / livros.size() * 100;

        model.addAttribute("taxaUtilizacao", String.format("%.1f", taxaUtilizacao));
        
        // KPIs Estáticos (Placeholder para futura implementação real)
        model.addAttribute("tempoMedioEmprestimo", "14"); 
        model.addAttribute("taxaRenovacao", "25"); 
        model.addAttribute("taxaAtraso", "5"); 
        
        // KPI: Média de livros lidos por usuário
        model.addAttribute("livrosPorUsuario", String.format("%.1f", usuarios.isEmpty() ? 0 : (double) todosEmprestimos.size() / usuarios.size()));

        // Lista os 10 empréstimos mais recentes para a tabela de auditoria
        List<Emprestimo> ultimosEmprestimos = todosEmprestimos.stream()
                .sorted(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed())
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("ultimosEmprestimos", ultimosEmprestimos);
        model.addAttribute("multas", multas);

        return "relatorios";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}