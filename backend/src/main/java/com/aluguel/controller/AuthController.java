package com.aluguel.controller;

import com.aluguel.dto.AuthResponse;
import com.aluguel.dto.LoginRequest;
import com.aluguel.dto.RegistroAgenteRequest;
import com.aluguel.dto.RegistroClienteRequest;
import com.aluguel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return auth.login(req);
    }

    @PostMapping("/registro/cliente")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrarCliente(@Valid @RequestBody RegistroClienteRequest req) {
        return auth.registrarCliente(req);
    }

    @PostMapping("/registro/agente")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrarAgente(@Valid @RequestBody RegistroAgenteRequest req) {
        return auth.registrarAgente(req);
    }
}
