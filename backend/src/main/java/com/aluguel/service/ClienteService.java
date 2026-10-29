package com.aluguel.service;

import com.aluguel.dto.ClienteRequest;
import com.aluguel.exception.AcessoNegadoException;
import com.aluguel.exception.NaoEncontradoException;
import com.aluguel.exception.NegocioException;
import com.aluguel.model.Cliente;
import com.aluguel.model.Rendimento;
import com.aluguel.model.Usuario;
import com.aluguel.repository.ClienteRepository;
import com.aluguel.repository.PedidoAluguelRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {
    private final ClienteRepository clientes;
    private final PedidoAluguelRepository pedidos;

    public ClienteService(ClienteRepository clientes, PedidoAluguelRepository pedidos) {
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    @Transactional
    public Cliente criar(Usuario usuario, ClienteRequest dados) {
        if (clientes.existsByCpf(dados.cpf())) throw new NegocioException("CPF ja cadastrado.");
        if (clientes.existsByRg(dados.rg())) throw new NegocioException("RG ja cadastrado.");
        var c = new Cliente();
        c.setUsuario(usuario);
        aplicar(c, dados);
        return clientes.save(c);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clientes.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscar(Long id, Authentication auth) {
        var c = clientes.findById(id).orElseThrow(() -> new NaoEncontradoException("Cliente nao encontrado."));
        verificarAcesso(c, auth);
        return c;
    }

    @Transactional(readOnly = true)
    public Cliente doUsuario(String login) {
        return clientes.findByUsuarioLogin(login)
                .orElseThrow(() -> new NaoEncontradoException("Cliente nao encontrado."));
    }

    @Transactional
    public Cliente atualizar(Long id, ClienteRequest dados, Authentication auth) {
        var c = buscar(id, auth);
        if (!c.getCpf().equals(dados.cpf()) && clientes.existsByCpf(dados.cpf()))
            throw new NegocioException("CPF ja cadastrado.");
        if (!c.getRg().equals(dados.rg()) && clientes.existsByRg(dados.rg()))
            throw new NegocioException("RG ja cadastrado.");
        aplicar(c, dados);
        return c;
    }

    @Transactional
    public void excluir(Long id, Authentication auth) {
        var c = buscar(id, auth);
        if (!pedidos.findByClienteId(id).isEmpty()) {
            throw new NegocioException("Cliente possui pedidos de aluguel e nao pode ser excluido.");
        }
        clientes.delete(c);
    }

    private void aplicar(Cliente c, ClienteRequest d) {
        c.setNome(d.nome());
        c.setRg(d.rg());
        c.setCpf(d.cpf());
        c.setEndereco(d.endereco());
        c.setProfissao(d.profissao());
        var novos = new ArrayList<Rendimento>();
        if (d.rendimentos() != null) {
            d.rendimentos().forEach(r -> novos.add(new Rendimento(r.entidadeEmpregadora(), r.valor())));
        }
        c.getRendimentos().clear();
        c.getRendimentos().addAll(novos);
    }

    /** Clientes so acessam o proprio cadastro; agentes acessam todos. */
    private void verificarAcesso(Cliente c, Authentication auth) {
        boolean cliente = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
        if (cliente && !c.getUsuario().getLogin().equals(auth.getName())) {
            throw new AcessoNegadoException("Acesso negado ao cadastro de outro cliente.");
        }
    }
}
