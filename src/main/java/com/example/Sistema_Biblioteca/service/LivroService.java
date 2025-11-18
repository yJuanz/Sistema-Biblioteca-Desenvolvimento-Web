package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.Livro;
import com.example.Sistema_Biblioteca.model.LivroAutor;
import com.example.Sistema_Biblioteca.model.LivroAutorId;
import com.example.Sistema_Biblioteca.model.Autor;
// import com.example.Sistema_Biblioteca.model.Categoria;
// import com.example.Sistema_Biblioteca.model.Editora;
import com.example.Sistema_Biblioteca.repository.LivroRepository;
import com.example.Sistema_Biblioteca.repository.AutorRepository;
// import com.example.Sistema_Biblioteca.repository.CategoriaRepository;
// import com.example.Sistema_Biblioteca.repository.EditoraRepository;
import com.example.Sistema_Biblioteca.repository.LivroAutorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    // @Autowired
    // private CategoriaRepository categoriaRepository;

    // @Autowired
    // private EditoraRepository editoraRepository;

    @Autowired
    private LivroAutorRepository livroAutorRepository;

    public List<Livro> listarTodos() {
        return livroRepository.findAll();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(isbn);
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Livro> buscarPorAutor(String autor) {
        // Esta query foi corrigida no LivroRepository para usar a junção LivroAutor
        return livroRepository.findByAutorNomeContaining(autor);
    }

    public List<Livro> listarDisponiveis() {
        return livroRepository.findLivrosDisponiveis();
    }

    public List<Livro> listarDisponiveisPorCategoria(String categoria) {
        return livroRepository.findLivrosDisponiveisPorCategoria(categoria);
    }

    public List<Livro> buscarPorPeriodoPublicacao(Integer anoInicio, Integer anoFim) {
        return livroRepository.findByAnoPublicacaoBetween(anoInicio, anoFim);
    }

    public Livro criar(Livro livro) {
        // Validação de ISBN único
        if (livroRepository.findByIsbn(livro.getIsbn()).isPresent()) {
            throw new RuntimeException("Já existe um livro com este ISBN");
        }

        // Garantir que exemplares disponíveis é igual à quantidade total
        if (livro.getExemplaresDisponiveis() == null) {
            livro.setExemplaresDisponiveis(livro.getQuantidadeExemplares());
        }

        return livroRepository.save(livro);
    }

    public Livro atualizar(Long id, Livro livroAtualizado) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(livroAtualizado.getTitulo());
                    livro.setIsbn(livroAtualizado.getIsbn());
                    livro.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
                    livro.setQuantidadeExemplares(livroAtualizado.getQuantidadeExemplares());

                    // Atualizar editora se fornecida
                    if (livroAtualizado.getEditora() != null) {
                        livro.setEditora(livroAtualizado.getEditora());
                    }

                    // Atualizar categoria se fornecida
                    if (livroAtualizado.getCategoria() != null) {
                        livro.setCategoria(livroAtualizado.getCategoria());
                    }

                    // ----- ESTE É O BLOCO QUE CAUSAVA O ERRO E FOI REMOVIDO -----
                    // if (livroAtualizado.getAutores() != null && !livroAtualizado.getAutores().isEmpty()) {
                    //    livro.setAutores(livroAtualizado.getAutores());
                    // }
                    // -------------------------------------------------------------
                    
                    // O gerenciamento de autores agora é feito separadamente
                    // através dos métodos adicionarAutor() e removerAutor().

                    return livroRepository.save(livro);
                })
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Verificar se livro tem empréstimos ativos
        if (livro.getExemplaresDisponiveis() < livro.getQuantidadeExemplares()) {
            throw new RuntimeException("Não é possível excluir livro com exemplares emprestados");
        }

        // A deleção do livro vai remover as associações em livro_autor
        // devido ao CascadeType.ALL na entidade Livro
        livroRepository.delete(livro);
    }

    public Livro adicionarAutor(Long livroId, Long autorId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Autor autor = autorRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado"));

        // Cria a chave composta
        LivroAutorId id = new LivroAutorId(livroId, autorId);
        
        // Cria a entidade de associação
        LivroAutor livroAutor = new LivroAutor(id, livro, autor);
        
        // Salva a associação
        livroAutorRepository.save(livroAutor);

        return livro;
    }

    public Livro removerAutor(Long livroId, Long autorId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        // Autor autor = autorRepository.findById(autorId)
        //         .orElseThrow(() -> new RuntimeException("Autor não encontrado"));

        // Cria a chave composta para buscar
        LivroAutorId id = new LivroAutorId(livroId, autorId);
        
        LivroAutor livroAutor = livroAutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associação Autor-Livro não encontrada"));

        // Deleta a associação
        livroAutorRepository.delete(livroAutor);

        return livro;
    }

    public Livro atualizarDisponibilidade(Long livroId, Integer novaQuantidade) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        int diferenca = novaQuantidade - livro.getQuantidadeExemplares();
        livro.setQuantidadeExemplares(novaQuantidade);
        livro.setExemplaresDisponiveis(livro.getExemplaresDisponiveis() + diferenca);

        return livroRepository.save(livro);
    }

    public boolean isDisponivelParaEmprestimo(Long livroId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        return livro.getExemplaresDisponiveis() > 0;
    }

    public Long contarLivrosDisponiveis() {
        return livroRepository.findLivrosDisponiveis().stream().count();
    }

    public List<Livro> buscarLivrosAvancado(String titulo, String autor, String categoria, Integer anoInicio, Integer anoFim) {
        // Implementação de busca avançada com múltiplos critérios
        if (titulo != null && !titulo.isEmpty()) {
            return livroRepository.findByTituloContainingIgnoreCase(titulo);
        } else if (autor != null && !autor.isEmpty()) {
            return livroRepository.findByAutorNomeContaining(autor);
        } else if (categoria != null && !categoria.isEmpty()) {
            return livroRepository.findLivrosDisponiveisPorCategoria(categoria);
        } else if (anoInicio != null && anoFim != null) {
            return livroRepository.findByAnoPublicacaoBetween(anoInicio, anoFim);
        }

        return livroRepository.findAll();
    }
}