package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository
        extends JpaRepository<Pagamento, Long> {

    boolean existsByPedido_Id(Long pedidoId);
}