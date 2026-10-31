package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}