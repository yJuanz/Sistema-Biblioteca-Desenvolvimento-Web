package com.example.Sistema_Biblioteca.dto;

import com.example.Sistema_Biblioteca.model.TipoUsuario;

public record RegisterDTO(String nome, String matricula, String email, String senha, TipoUsuario tipo) {
}