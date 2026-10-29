package com.aluguel.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Parecer financeiro de um agente. Dados de credito so sao aceitos de agentes do tipo BANCO. */
public record AvaliacaoRequest(
        @NotNull Boolean aprovado,
        String parecer,
        BigDecimal valorFinanciado,
        BigDecimal taxaJurosMensal) {}
