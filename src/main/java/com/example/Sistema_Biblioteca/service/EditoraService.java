package com.example.Sistema_Biblioteca.service;

import com.example.Sistema_Biblioteca.model.Editora;
import com.example.Sistema_Biblioteca.repository.EditoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditoraService {

    @Autowired
    private EditoraRepository editoraRepository;

    public List<Editora> listarTodos() {
        return editoraRepository.findAll();
    }

    public Optional<Editora> buscarPorId(Long id) {
        return editoraRepository.findById(id);
    }

    public Optional<Editora> buscarPorNome(String nome) {
        return editoraRepository.findByNome(nome);
    }

    public Editora criar(Editora editora) {
        return editoraRepository.save(editora);
    }

    public Editora atualizar(Long id, Editora editoraAtualizada) {
        return editoraRepository.findById(id)
                .map(editora -> {
                    editora.setNome(editoraAtualizada.getNome());
                    editora.setEndereco(editoraAtualizada.getEndereco());
                    editora.setTelefone(editoraAtualizada.getTelefone());
                    return editoraRepository.save(editora);
                })
                .orElseThrow(() -> new RuntimeException("Editora não encontrada"));
    }

    public void deletar(Long id) {
        Editora editora = editoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Editora não encontrada"));

        editoraRepository.delete(editora);
    }
}