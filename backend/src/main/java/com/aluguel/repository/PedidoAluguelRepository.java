package com.aluguel.repository;

import com.aluguel.model.PedidoAluguel;
import com.aluguel.model.StatusPedido;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoAluguelRepository extends JpaRepository<PedidoAluguel, Long> {
    List<PedidoAluguel> findByClienteId(Long clienteId);

    @Query("""
        select count(p) > 0 from PedidoAluguel p
        where p.automovel.id = :automovelId
          and p.status in :ativos
          and p.dataInicio <= :fim and p.dataFim >= :inicio
          and (:ignorarId is null or p.id <> :ignorarId)
        """)
    boolean existeConflito(@Param("automovelId") Long automovelId,
                           @Param("inicio") LocalDate inicio,
                           @Param("fim") LocalDate fim,
                           @Param("ativos") List<StatusPedido> ativos,
                           @Param("ignorarId") Long ignorarId);
}
