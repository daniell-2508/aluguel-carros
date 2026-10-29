package com.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroClienteRequest(
        @NotBlank @Size(min = 3) String login,
        @NotBlank @Size(min = 6, message = "minimo de 6 caracteres") String senha,
        @NotNull @Valid ClienteRequest dados) {}
