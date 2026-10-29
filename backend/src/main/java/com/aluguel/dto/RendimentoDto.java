package com.aluguel.dto;

import com.aluguel.model.Rendimento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record RendimentoDto(@NotBlank String entidadeEmpregadora, @NotNull @DecimalMin("0.0") BigDecimal valor) {
    public static RendimentoDto de(Rendimento r) {
        return new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor());
    }
}
