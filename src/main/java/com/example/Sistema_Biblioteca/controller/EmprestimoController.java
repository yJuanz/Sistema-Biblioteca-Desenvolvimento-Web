package com.example.Sistema_Biblioteca.controller;

import com.example.Sistema_Biblioteca.dto.EmprestimoRequestDTO;
import com.example.Sistema_Biblioteca.model.Emprestimo;
import com.example.Sistema_Biblioteca.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestão de Empréstimos.
 * Diferente do CrudController, esta classe retorna dados em formato JSON (API),
 * permitindo que sistemas externos ou front-ends modernos consumam nossa lógica.
 */
@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    /**
     * Realiza um novo empréstimo.
     * Recebe um DTO (Data Transfer Object) para desacoplar a requisição externa
     * da nossa entidade de banco de dados.
     * * @param request Objeto contendo apenas os IDs do usuário e do livro.
     * @return 200 OK com o empréstimo criado ou 400 Bad Request com o erro.
     */
    @PostMapping("/realizar")
    public ResponseEntity<?> realizarEmprestimo(@RequestBody EmprestimoRequestDTO request) {
        try {
            // Delega a regra de negócio (validação de estoque, multas, etc) para o Service
            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(
                    request.getUsuarioId(),
                    request.getLivroId()
            );
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            // Tratamento de exceção: Retorna mensagem amigável se algo falhar (ex: livro indisponível)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Registra a devolução de um livro.
     * Utilizamos o verbo PUT pois estamos alterando o estado de um recurso existente.
     */
    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolverLivro(@PathVariable Long id) {
        try {
            // O serviço calcula multas automaticamente se houver atraso
            Emprestimo emprestimo = emprestimoService.devolverLivro(id);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint para relatórios e dashboards.
     * Retorna lista de todos os empréstimos que ainda não foram devolvidos.
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosAtivos();
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Endpoint crítico para gestão de inadimplência.
     * Filtra empréstimos onde a data atual é superior à data de devolução prevista.
     */
    @GetMapping("/atrasados")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosAtrasados() {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosAtrasados();
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Histórico do usuário.
     * Permite consultar todos os empréstimos vinculados a um ID específico.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosPorUsuario(@PathVariable Long usuarioId) {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosPorUsuario(usuarioId);
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Busca detalhes de um empréstimo específico.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarPorId(@PathVariable Long id) {
        try {
            // Nota: Em uma implementação futura, podemos adicionar findById no service.
            // Por enquanto, retornamos 404 para indicar que o endpoint existe mas não localizou.
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}