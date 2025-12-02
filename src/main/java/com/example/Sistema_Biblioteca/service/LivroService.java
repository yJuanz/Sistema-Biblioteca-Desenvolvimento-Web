package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.*;
import com.example.Sistema_Biblioteca.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio dos Livros.
 * Centraliza as operações de criar, atualizar e excluir, garantindo a integridade dos dados.
 */
@Service
public class LivroService {

    // Repositórios injetados para acesso ao banco de dados
    @Autowired private LivroRepository livroRepository;
    @Autowired private AutorRepository autorRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private EditoraRepository editoraRepository;
    @Autowired private LivroAutorRepository livroAutorRepository;
    
    // Injetados para realizar a limpeza de dados relacionados antes da exclusão
    @Autowired private EmprestimoRepository emprestimoRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private MultaRepository multaRepository;

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public List<Livro> listarDisponiveis() {
        // Busca personalizada: traz apenas livros com exemplares > 0
        return livroRepository.findLivrosDisponiveis();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    /**
     * Cria um livro completo, gerenciando automaticamente as dependências.
     * Se o Autor, Categoria ou Editora não existirem, eles são criados na hora.
     */
    @Transactional // Garante que todas as operações ocorram na mesma transação (Rollback em caso de erro)
    public void criarLivroCompleto(String titulo, String isbn, Integer ano, Integer qtd,
                                   String nomeAutor, String nomeCategoria, String nomeEditora) {

        // Validação de negócio: ISBN deve ser único
        if (livroRepository.findByIsbn(isbn).isPresent()) {
            throw new RuntimeException("Já existe um livro cadastrado com este ISBN.");
        }

        // Lógica "Find or Create": Busca pelo nome, se não achar, salva um novo registro.
        Categoria categoria = categoriaRepository.findByNome(nomeCategoria)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nomeCategoria, "Criada automaticamente")));

        Editora editora = editoraRepository.findByNome(nomeEditora)
                .orElseGet(() -> editoraRepository.save(new Editora(nomeEditora, "Endereço Padrão", "0000-0000")));

        Autor autor = autorRepository.findByNome(nomeAutor)
                .orElseGet(() -> autorRepository.save(new Autor(nomeAutor, "Desconhecida", null)));

        // Montagem do objeto Livro
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(ano);
        livro.setQuantidadeExemplares(qtd);
        livro.setExemplaresDisponiveis(qtd); // Inicialmente, todos disponíveis
        livro.setCategoria(categoria);
        livro.setEditora(editora);

        // Salva o livro primeiro para gerar o ID
        livro = livroRepository.save(livro);

        // Cria o relacionamento N:N na tabela associativa 'livro_autor'
        LivroAutorId idVinculo = new LivroAutorId(livro.getId(), autor.getId());
        LivroAutor vinculo = new LivroAutor(idVinculo, livro, autor);
        
        livroAutorRepository.save(vinculo);
    }

    /**
     * Atualiza um livro existente, recalcula o estoque e ajusta relacionamentos.
     */
    @Transactional
    public void atualizarCompleto(Long id, String titulo, String isbn, Integer ano, Integer qtd,
                                  String nomeAutor, String nomeCategoria, String nomeEditora) {
        
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Atualização de campos simples
        livro.setTitulo(titulo);
        livro.setIsbn(isbn);
        livro.setAnoPublicacao(ano);
        
        // Lógica de Estoque: Se a quantidade total mudou, ajusta os disponíveis proporcionalmente
        int diferenca = qtd - livro.getQuantidadeExemplares();
        livro.setQuantidadeExemplares(qtd);
        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() + diferenca);

        // Atualização de Relacionamentos (Categoria e Editora)
        Categoria categoria = categoriaRepository.findByNome(nomeCategoria)
                .orElseGet(() -> categoriaRepository.save(new Categoria(nomeCategoria, "Criada automaticamente")));
        livro.setCategoria(categoria);

        Editora editora = editoraRepository.findByNome(nomeEditora)
                .orElseGet(() -> editoraRepository.save(new Editora(nomeEditora, "Endereço Padrão", "0000-0000")));
        livro.setEditora(editora);

        // Atualização de Autor (Complexo por ser N:N)
        Autor autorNovo = autorRepository.findByNome(nomeAutor)
                .orElseGet(() -> autorRepository.save(new Autor(nomeAutor, "Desconhecida", null)));

        // Remove vínculos antigos
        if (!livro.getLivroAutores().isEmpty()) {
            livroAutorRepository.deleteAll(livro.getLivroAutores());
            livro.getLivroAutores().clear();
            livroRepository.saveAndFlush(livro); // Força a sincronização com o banco
        }

        // Cria novo vínculo
        LivroAutorId idVinculo = new LivroAutorId(livro.getId(), autorNovo.getId());
        LivroAutor novoVinculo = new LivroAutor(idVinculo, livro, autorNovo);
        livroAutorRepository.save(novoVinculo);
        
        livroRepository.save(livro);
    }

    // Método simples mantido para compatibilidade, se necessário
    public Livro atualizar(Long id, Livro livroAtualizado) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(livroAtualizado.getTitulo());
                    livro.setIsbn(livroAtualizado.getIsbn());
                    livro.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
                    livro.setQuantidadeExemplares(livroAtualizado.getQuantidadeExemplares());
                    return livroRepository.save(livro);
                })
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    /**
     * Exclusão segura de livro com limpeza de histórico.
     * Remove reservas, empréstimos antigos e multas antes de apagar o livro.
     */
    @Transactional
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        
        // Regra de Negócio: Não pode excluir se houver livro emprestado na rua
        if (livro.getExemplaresDisponiveis() < livro.getQuantidadeExemplares()) {
            throw new RuntimeException("Não é possível excluir: existem exemplares emprestados.");
        }
        
        // 1. Limpeza de Reservas associadas
        List<Reserva> reservas = reservaRepository.findByLivro(livro);
        reservaRepository.deleteAll(reservas);

        // 2. Limpeza de Histórico de Empréstimos e Multas
        // Percorre a lista de empréstimos (carregada na sessão transacional)
        if (livro.getEmprestimos() != null && !livro.getEmprestimos().isEmpty()) {
             for (Emprestimo e : livro.getEmprestimos()) {
                 if (e.getMulta() != null) {
                     multaRepository.delete(e.getMulta()); // Apaga a multa primeiro
                 }
                 emprestimoRepository.delete(e); // Depois o empréstimo
             }
        }

        // 3. Exclui o Livro (o vínculo livro_autor é removido por Cascade)
        livroRepository.delete(livro);
    }
}