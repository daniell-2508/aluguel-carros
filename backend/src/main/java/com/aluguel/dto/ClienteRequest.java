package com.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Dados do cliente (usados no cadastro e na atualizacao). */
public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank String rg,
        @NotBlank String cpf,
        @NotBlank String endereco,
        String profissao,
        @Valid @Size(max = 3, message = "maximo de 3 rendimentos") List<RendimentoDto> rendimentos) {}
