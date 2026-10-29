package com.aluguel.dto;

import com.aluguel.model.Cliente;
import java.util.List;

public record ClienteResponse(Long id, String login, String nome, String rg, String cpf, String endereco,
                              String profissao, List<RendimentoDto> rendimentos) {
    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getUsuario().getLogin(), c.getNome(), c.getRg(), c.getCpf(),
                c.getEndereco(), c.getProfissao(), c.getRendimentos().stream().map(RendimentoDto::de).toList());
    }
}
