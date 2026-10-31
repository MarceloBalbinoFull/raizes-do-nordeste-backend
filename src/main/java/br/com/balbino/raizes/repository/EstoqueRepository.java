
package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Estoque;
import br.com.balbino.raizes.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProduto_IdAndUnidade_Id(
            Long produtoId,
            Long unidadeId
    );

    @Query("""
            SELECT p
            FROM Estoque e
            JOIN e.produto p
            WHERE e.unidade.id = :unidadeId
              AND e.quantidade > 0
              AND p.ativo = true
            ORDER BY p.nome ASC
            """)
    List<Produto> findProdutosDisponiveisPorUnidade(
            @Param("unidadeId") Long unidadeId
    );
}
