package com.aluguel.service;

import com.aluguel.dto.AuthResponse;
import com.aluguel.dto.LoginRequest;
import com.aluguel.dto.RegistroAgenteRequest;
import com.aluguel.dto.RegistroClienteRequest;
import com.aluguel.exception.NegocioException;
import com.aluguel.model.Agente;
import com.aluguel.model.Perfil;
import com.aluguel.model.Usuario;
import com.aluguel.repository.AgenteRepository;
import com.aluguel.repository.ClienteRepository;
import com.aluguel.repository.UsuarioRepository;
import com.aluguel.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;
    private final ClienteService clienteService;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository usuarios, ClienteRepository clientes, AgenteRepository agentes,
                       ClienteService clienteService, PasswordEncoder encoder, JwtService jwt) {
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.agentes = agentes;
        this.clienteService = clienteService;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Usuario u = usuarios.findByLogin(req.login())
                .filter(x -> encoder.matches(req.senha(), x.getSenha()))
                .orElseThrow(() -> new NegocioException("Usuario ou senha invalidos."));
        return resposta(u);
    }

    @Transactional
    public AuthResponse registrarCliente(RegistroClienteRequest req) {
        if (usuarios.existsByLogin(req.login())) {
            throw new NegocioException("Login ja utilizado.");
        }
        var cliente = clienteService.criar(new Usuario(req.login(), encoder.encode(req.senha()), Perfil.CLIENTE),
                req.dados());
        return resposta(cliente.getUsuario());
    }

    @Transactional
    public AuthResponse registrarAgente(RegistroAgenteRequest req) {
        if (req.tipo() == Perfil.CLIENTE) {
            throw new NegocioException("O tipo do agente deve ser EMPRESA ou BANCO.");
        }
        if (usuarios.existsByLogin(req.login())) {
            throw new NegocioException("Login ja utilizado.");
        }
        if (agentes.existsByCnpj(req.cnpj())) {
            throw new NegocioException("CNPJ ja cadastrado.");
        }
        var agente = new Agente();
        agente.setNome(req.nome());
        agente.setCnpj(req.cnpj());
        agente.setUsuario(new Usuario(req.login(), encoder.encode(req.senha()), req.tipo()));
        return resposta(agentes.save(agente).getUsuario());
    }

    private AuthResponse resposta(Usuario u) {
        String nome = u.getPerfil() == Perfil.CLIENTE
                ? clientes.findByUsuarioLogin(u.getLogin()).map(c -> c.getNome()).orElse(u.getLogin())
                : agentes.findByUsuarioLogin(u.getLogin()).map(a -> a.getNome()).orElse(u.getLogin());
        return new AuthResponse(jwt.gerar(u.getLogin(), u.getPerfil().name()), u.getLogin(), u.getPerfil(), nome);
    }
}
