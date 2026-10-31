package br.com.balbino.raizes.repository;

import br.com.balbino.raizes.domain.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FidelidadeRepository
        extends JpaRepository<Fidelidade, Long> {

    Optional<Fidelidade> findByUsuario_Id(Long usuarioId);

    boolean existsByUsuario_Id(Long usuarioId);
}