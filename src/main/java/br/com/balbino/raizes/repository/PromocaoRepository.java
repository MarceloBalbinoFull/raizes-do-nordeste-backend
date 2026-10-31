
package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PromocaoRepository
        extends JpaRepository<Promocao, Long> {

    List<Promocao>
    findByAtivoTrueAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            LocalDate dataInicio,
            LocalDate dataFim
    );
}
