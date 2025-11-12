package com.biblioteca.service;

import com.biblioteca.model.Usuario;
import com.biblioteca.model.TipoUsuario;
import com.biblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContaining(nome);
    }

    public List<Usuario> buscarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipo(tipo);
    }

    public Usuario criar(Usuario usuario) {
        try {
            // Validações básicas
            if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
                throw new RuntimeException("Nome é obrigatório");
            }
            if (usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty()) {
                throw new RuntimeException("Matrícula é obrigatória");
            }
            if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Email é obrigatório");
            }
            if (usuario.getTipo() == null) {
                throw new RuntimeException("Tipo de usuário é obrigatório");
            }

            // Verificar se matrícula já existe
            if (usuarioRepository.findByMatricula(usuario.getMatricula()).isPresent()) {
                throw new RuntimeException("Já existe um usuário com esta matrícula");
            }

            // Verificar se email já existe
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                throw new RuntimeException("Já existe um usuário com este email");
            }

            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    try {
                        usuario.setNome(usuarioAtualizado.getNome());
                        usuario.setEmail(usuarioAtualizado.getEmail());
                        usuario.setTipo(usuarioAtualizado.getTipo());
                        return usuarioRepository.save(usuario);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage());
                    }
                })
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void deletar(Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            // Verificar se usuário tem empréstimos ativos
            Long emprestimosAtivos = usuarioRepository.countEmprestimosAtivosPorUsuario(usuario);
            if (emprestimosAtivos > 0) {
                throw new RuntimeException("Não é possível excluir usuário com empréstimos ativos");
            }

            usuarioRepository.delete(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir usuário: " + e.getMessage());
        }
    }

    public boolean usuarioPodeEmprestar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Long emprestimosAtivos = usuarioRepository.countEmprestimosAtivosPorUsuario(usuario);
        return emprestimosAtivos < 3; // Limite de 3 empréstimos
    }

    public Long contarEmprestimosAtivos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return usuarioRepository.countEmprestimosAtivosPorUsuario(usuario);
    }
}