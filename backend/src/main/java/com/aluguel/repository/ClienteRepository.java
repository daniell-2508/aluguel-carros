package com.aluguel.repository;

import com.aluguel.model.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioLogin(String login);
    boolean existsByCpf(String cpf);
    boolean existsByRg(String rg);
}
