package com.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/** Contrato de credito concedido por um banco agente, associado a um aluguel. */
@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Agente banco;

    @Column(nullable = false)
    private BigDecimal valorFinanciado;

    @Column(nullable = false)
    private BigDecimal taxaJurosMensal;

    public Long getId() { return id; }
    public Agente getBanco() { return banco; }
    public void setBanco(Agente banco) { this.banco = banco; }
    public BigDecimal getValorFinanciado() { return valorFinanciado; }
    public void setValorFinanciado(BigDecimal v) { this.valorFinanciado = v; }
    public BigDecimal getTaxaJurosMensal() { return taxaJurosMensal; }
    public void setTaxaJurosMensal(BigDecimal t) { this.taxaJurosMensal = t; }
}
