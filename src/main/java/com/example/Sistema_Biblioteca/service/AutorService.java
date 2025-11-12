package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.Autor;
import com.example.Sistema_Biblioteca.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarTodos() {
        return autorRepository.findAll();
    }

    public Optional<Autor> buscarPorId(Long id) {
        return autorRepository.findById(id);
    }

    public Optional<Autor> buscarPorNome(String nome) {
        return autorRepository.findByNome(nome);
    }

    public List<Autor> buscarPorNomeContendo(String nome) {
        return autorRepository.findByNomeContaining(nome);
    }

    public List<Autor> buscarPorNacionalidade(String nacionalidade) {
        return autorRepository.findByNacionalidade(nacionalidade);
    }

    public Autor criar(Autor autor) {
        return autorRepository.save(autor);
    }

    public Autor atualizar(Long id, Autor autorAtualizado) {
        return autorRepository.findById(id)
                .map(autor -> {
                    autor.setNome(autorAtualizado.getNome());
                    autor.setNacionalidade(autorAtualizado.getNacionalidade());
                    autor.setDataNascimento(autorAtualizado.getDataNascimento());
                    return autorRepository.save(autor);
                })
                .orElseThrow(() -> new RuntimeException("Autor não encontrado"));
    }

    public void deletar(Long id) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Autor não encontrado"));

        autorRepository.delete(autor);
    }
}