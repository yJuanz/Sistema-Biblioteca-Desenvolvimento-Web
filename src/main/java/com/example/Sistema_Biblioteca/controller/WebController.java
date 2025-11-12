package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.model.Livro;
import com.example.Sistema_Biblioteca.model.Usuario;
import com.example.Sistema_Biblioteca.model.Emprestimo;
import com.example.Sistema_Biblioteca.service.LivroService;
import com.example.Sistema_Biblioteca.service.UsuarioService;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private LivroService livroService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmprestimoService emprestimoService;

    @GetMapping("/")
    public String home(Model model) {
        // Estatísticas para a página inicial
        List<Livro> livros = livroService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> emprestimosAtrasados = emprestimoService.listarEmprestimosAtrasados();

        // Livros mais populares (com mais empréstimos)
        List<Livro> livrosPopulares = livros.stream()
                .sorted((l1, l2) -> Integer.compare(l2.getEmprestimos().size(), l1.getEmprestimos().size()))
                .limit(5)
                .collect(Collectors.toList());

        // Últimos empréstimos
        List<Emprestimo> ultimosEmprestimos = emprestimosAtivos.stream()
                .sorted(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("emprestimosAtivos", emprestimosAtivos.size());
        model.addAttribute("emprestimosAtrasados", emprestimosAtrasados.size());
        model.addAttribute("livrosPopulares", livrosPopulares);
        model.addAttribute("ultimosEmprestimos", ultimosEmprestimos);

        return "index";
    }

    @GetMapping("/livros")
    public String livros(Model model) {
        List<Livro> livros = livroService.listarTodos();
        long livrosDisponiveis = livros.stream().filter(l -> l.getExemplaresDisponiveis() > 0).count();
        long livrosIndisponiveis = livros.stream().filter(l -> l.getExemplaresDisponiveis() == 0).count();
        long categoriasCount = livros.stream().map(l -> l.getCategoria()).distinct().count();

        model.addAttribute("livros", livros);
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("livrosDisponiveis", livrosDisponiveis);
        model.addAttribute("livrosIndisponiveis", livrosIndisponiveis);
        model.addAttribute("categoriasCount", categoriasCount);

        return "livros";
    }

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

    @GetMapping("/emprestimos")
    public String emprestimos(Model model) {
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> emprestimosAtrasados = emprestimoService.listarEmprestimosAtrasados();

        // Calcular total de multas pendentes
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

    @GetMapping("/reservas")
    public String reservas(Model model) {
        // Em uma implementação real, você buscaria as reservas do serviço
        // Por enquanto, vamos usar dados vazios para demonstração

        model.addAttribute("totalReservas", 0);
        model.addAttribute("reservasAtivasCount", 0);
        model.addAttribute("reservasExpiradasCount", 0);
        model.addAttribute("livrosMaisReservadosCount", 0);

        // Para o formulário de nova reserva
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("livrosDisponiveis", livroService.listarDisponiveis());

        return "reservas";
    }

    @GetMapping("/relatorios")
    public String relatorios(Model model) {
        List<Livro> livros = livroService.listarTodos();
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<Emprestimo> emprestimosAtivos = emprestimoService.listarEmprestimosAtivos();
        List<Emprestimo> todosEmprestimos = emprestimoService.listarEmprestimosAtivos(); // Em real, buscar todos

        // Estatísticas básicas
        model.addAttribute("totalLivros", livros.size());
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("totalEmprestimos", todosEmprestimos.size());

        // Métricas calculadas (exemplo)
        double taxaUtilizacao = livros.isEmpty() ? 0 :
                (double) livros.stream().filter(l -> l.getExemplaresDisponiveis() < l.getQuantidadeExemplares()).count() / livros.size() * 100;

        model.addAttribute("taxaUtilizacao", String.format("%.1f", taxaUtilizacao));
        model.addAttribute("tempoMedioEmprestimo", "14");
        model.addAttribute("taxaRenovacao", "25");
        model.addAttribute("taxaAtraso", "5");
        model.addAttribute("livrosPorUsuario", String.format("%.1f", usuarios.isEmpty() ? 0 : (double) todosEmprestimos.size() / usuarios.size()));

        // Últimos empréstimos para o relatório detalhado
        List<Emprestimo> ultimosEmprestimos = todosEmprestimos.stream()
                .sorted(Comparator.comparing(Emprestimo::getDataEmprestimo).reversed())
                .limit(10)
                .collect(Collectors.toList());

        model.addAttribute("ultimosEmprestimos", ultimosEmprestimos);

        return "relatorios";
    }
}