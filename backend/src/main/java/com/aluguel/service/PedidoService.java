package com.aluguel.service;

import com.aluguel.dto.AvaliacaoRequest;
import com.aluguel.dto.PedidoRequest;
import com.aluguel.exception.AcessoNegadoException;
import com.aluguel.exception.NaoEncontradoException;
import com.aluguel.exception.NegocioException;
import com.aluguel.model.Agente;
import com.aluguel.model.ContratoCredito;
import com.aluguel.model.Perfil;
import com.aluguel.model.PedidoAluguel;
import com.aluguel.model.StatusPedido;
import com.aluguel.repository.AgenteRepository;
import com.aluguel.repository.AutomovelRepository;
import com.aluguel.repository.ClienteRepository;
import com.aluguel.repository.PedidoAluguelRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras dos pedidos de aluguel.
 * Fluxo: EM_ANALISE -> APROVADO | REPROVADO (agente) -> CONTRATADO (agente); CANCELADO (cliente/agente) a partir
 * de EM_ANALISE ou APROVADO.
 */
@Service
public class PedidoService {
    private static final List<StatusPedido> ATIVOS = List.of(StatusPedido.EM_ANALISE, StatusPedido.APROVADO,
            StatusPedido.CONTRATADO);

    private final PedidoAluguelRepository pedidos;
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;
    private final AutomovelRepository automoveis;

    public PedidoService(PedidoAluguelRepository pedidos, ClienteRepository clientes, AgenteRepository agentes,
                         AutomovelRepository automoveis) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.agentes = agentes;
        this.automoveis = automoveis;
    }

    @Transactional
    public PedidoAluguel criar(PedidoRequest r, Authentication auth) {
        var cliente = clientes.findByUsuarioLogin(auth.getName())
                .orElseThrow(() -> new AcessoNegadoException("Apenas clientes podem criar pedidos."));
        var p = new PedidoAluguel();
        p.setCliente(cliente);
        aplicar(p, r);
        return pedidos.save(p);
    }

    @Transactional(readOnly = true)
    public List<PedidoAluguel> listar(Authentication auth) {
        if (ehAgente(auth)) return pedidos.findAll();
        var cliente = clientes.findByUsuarioLogin(auth.getName())
                .orElseThrow(() -> new NaoEncontradoException("Cliente nao encontrado."));
        return pedidos.findByClienteId(cliente.getId());
    }

    @Transactional(readOnly = true)
    public PedidoAluguel buscar(Long id, Authentication auth) {
        var p = pedidos.findById(id).orElseThrow(() -> new NaoEncontradoException("Pedido nao encontrado."));
        if (!ehAgente(auth) && !p.getCliente().getUsuario().getLogin().equals(auth.getName())) {
            throw new AcessoNegadoException("Acesso negado ao pedido de outro cliente.");
        }
        return p;
    }

    /** Clientes e agentes podem modificar um pedido enquanto ele estiver em analise. */
    @Transactional
    public PedidoAluguel modificar(Long id, PedidoRequest r, Authentication auth) {
        var p = buscar(id, auth);
        if (p.getStatus() != StatusPedido.EM_ANALISE) {
            throw new NegocioException("Apenas pedidos em analise podem ser modificados.");
        }
        aplicar(p, r);
        return p;
    }

    @Transactional
    public PedidoAluguel cancelar(Long id, Authentication auth) {
        var p = buscar(id, auth);
        if (p.getStatus() != StatusPedido.EM_ANALISE && p.getStatus() != StatusPedido.APROVADO) {
            throw new NegocioException("O pedido nao pode mais ser cancelado (status " + p.getStatus() + ").");
        }
        p.setStatus(StatusPedido.CANCELADO);
        return p;
    }

    @Transactional
    public PedidoAluguel avaliar(Long id, AvaliacaoRequest r, Authentication auth) {
        var p = buscar(id, auth);
        if (p.getStatus() != StatusPedido.EM_ANALISE) {
            throw new NegocioException("Apenas pedidos em analise podem ser avaliados.");
        }
        Agente agente = agenteLogado(auth);
        p.setAvaliador(agente);
        p.setParecer(r.parecer());
        if (!r.aprovado()) {
            p.setStatus(StatusPedido.REPROVADO);
            return p;
        }
        p.setStatus(StatusPedido.APROVADO);
        if (r.valorFinanciado() != null || r.taxaJurosMensal() != null) {
            if (agente.getUsuario().getPerfil() != Perfil.BANCO) {
                throw new NegocioException("Apenas bancos podem conceder contrato de credito.");
            }
            if (r.valorFinanciado() == null || r.taxaJurosMensal() == null
                    || r.valorFinanciado().signum() <= 0 || r.taxaJurosMensal().signum() < 0) {
                throw new NegocioException("Informe valor financiado (> 0) e taxa de juros (>= 0).");
            }
            var cc = new ContratoCredito();
            cc.setBanco(agente);
            cc.setValorFinanciado(r.valorFinanciado());
            cc.setTaxaJurosMensal(r.taxaJurosMensal());
            p.setContratoCredito(cc);
        }
        return p;
    }

    /** Execucao do contrato: somente pedidos aprovados financeiramente. */
    @Transactional
    public PedidoAluguel contratar(Long id, Authentication auth) {
        var p = buscar(id, auth);
        agenteLogado(auth);
        if (p.getStatus() != StatusPedido.APROVADO) {
            throw new NegocioException("Apenas pedidos aprovados podem ter o contrato executado.");
        }
        p.setStatus(StatusPedido.CONTRATADO);
        return p;
    }

    private void aplicar(PedidoAluguel p, PedidoRequest r) {
        if (r.dataInicio().isAfter(r.dataFim())) {
            throw new NegocioException("A data de inicio deve ser anterior ou igual a data de fim.");
        }
        if (r.dataInicio().isBefore(LocalDate.now())) {
            throw new NegocioException("A data de inicio nao pode estar no passado.");
        }
        var auto = automoveis.findById(r.automovelId())
                .orElseThrow(() -> new NaoEncontradoException("Automovel nao encontrado."));
        if (pedidos.existeConflito(auto.getId(), r.dataInicio(), r.dataFim(), ATIVOS, p.getId())) {
            throw new NegocioException("O automovel ja esta reservado no periodo informado.");
        }
        p.setAutomovel(auto);
        p.setDataInicio(r.dataInicio());
        p.setDataFim(r.dataFim());
        p.setValorTotal(r.valorTotal());
    }

    private Agente agenteLogado(Authentication auth) {
        return agentes.findByUsuarioLogin(auth.getName())
                .orElseThrow(() -> new AcessoNegadoException("Operacao restrita a agentes."));
    }

    private boolean ehAgente(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> !a.getAuthority().equals("ROLE_CLIENTE"));
    }
}
