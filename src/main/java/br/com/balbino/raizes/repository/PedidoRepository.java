
package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCanalPedidoIgnoreCase(String canalPedido);

    List<Pedido> findByUsuario_Id(Long usuarioId);

    List<Pedido> findByUsuario_IdAndCanalPedidoIgnoreCase(
            Long usuarioId,
            String canalPedido
    );
}
