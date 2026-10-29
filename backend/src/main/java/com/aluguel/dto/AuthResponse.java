package com.aluguel.dto;

import com.aluguel.model.Perfil;

public record AuthResponse(String token, String login, Perfil perfil, String nome) {}
