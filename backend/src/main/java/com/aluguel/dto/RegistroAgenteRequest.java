package com.aluguel.dto;

import com.aluguel.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroAgenteRequest(
        @NotBlank @Size(min = 3) String login,
        @NotBlank @Size(min = 6) String senha,
        @NotBlank String nome,
        @NotBlank String cnpj,
        @NotNull Perfil tipo) {}
