package com.aluguel.controller;

import com.aluguel.dto.AvaliacaoRequest;
import com.aluguel.dto.PedidoRequest;
import com.aluguel.dto.PedidoResponse;
import com.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest req, Authentication auth) {
        return PedidoResponse.de(service.criar(req, auth));
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication auth) {
        return service.listar(auth).stream().map(PedidoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, Authentication auth) {
        return PedidoResponse.de(service.buscar(id, auth));
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @Valid @RequestBody PedidoRequest req,
                                    Authentication auth) {
        return PedidoResponse.de(service.modificar(id, req, auth));
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, Authentication auth) {
        return PedidoResponse.de(service.cancelar(id, auth));
    }

    @PostMapping("/{id}/avaliacao")
    @PreAuthorize("!hasRole('CLIENTE')")
    public PedidoResponse avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest req,
                                  Authentication auth) {
        return PedidoResponse.de(service.avaliar(id, req, auth));
    }

    @PostMapping("/{id}/contrato")
    @PreAuthorize("!hasRole('CLIENTE')")
    public PedidoResponse contratar(@PathVariable Long id, Authentication auth) {
        return PedidoResponse.de(service.contratar(id, auth));
    }
}
