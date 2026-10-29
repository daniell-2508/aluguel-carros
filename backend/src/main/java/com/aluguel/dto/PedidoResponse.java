package com.aluguel.dto;

import com.aluguel.model.PedidoAluguel;
import com.aluguel.model.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(Long id, Long clienteId, String clienteNome, Long automovelId, String automovel,
                             LocalDate dataInicio, LocalDate dataFim, BigDecimal valorTotal, StatusPedido status,
                             String avaliador, String parecer, BigDecimal valorFinanciado,
                             BigDecimal taxaJurosMensal, String bancoCredito, LocalDateTime criadoEm) {
    public static PedidoResponse de(PedidoAluguel p) {
        var cc = p.getContratoCredito();
        var a = p.getAutomovel();
        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(), a.getId(),
                a.getMarca() + " " + a.getModelo() + " (" + a.getPlaca() + ")",
                p.getDataInicio(), p.getDataFim(), p.getValorTotal(), p.getStatus(),
                p.getAvaliador() == null ? null : p.getAvaliador().getNome(), p.getParecer(),
                cc == null ? null : cc.getValorFinanciado(), cc == null ? null : cc.getTaxaJurosMensal(),
                cc == null ? null : cc.getBanco().getNome(), p.getCriadoEm());
    }
}
