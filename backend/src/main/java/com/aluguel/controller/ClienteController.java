package com.aluguel.controller;

import com.aluguel.dto.ClienteRequest;
import com.aluguel.dto.ClienteResponse;
import com.aluguel.service.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** CRUD de cliente. O "Create" acontece em POST /api/auth/registro/cliente (cadastro com login). */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("!hasRole('CLIENTE')")
    public List<ClienteResponse> listar() {
        return service.listar().stream().map(ClienteResponse::de).toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse meuCadastro(Authentication auth) {
        return ClienteResponse.de(service.doUsuario(auth.getName()));
    }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, Authentication auth) {
        return ClienteResponse.de(service.buscar(id, auth));
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest req,
                                     Authentication auth) {
        return ClienteResponse.de(service.atualizar(id, req, auth));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id, Authentication auth) {
        service.excluir(id, auth);
    }
}
