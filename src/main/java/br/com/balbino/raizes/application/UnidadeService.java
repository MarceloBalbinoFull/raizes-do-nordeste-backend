package br.com.balbino.raizes.application;

import br.com.balbino.raizes.domain.Unidade;
import br.com.balbino.raizes.repository.UnidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeRepository repository;

    public UnidadeService(UnidadeRepository repository) {
        this.repository = repository;
    }

    public Unidade salvar(Unidade unidade) {
        if (unidade.getAtivo() == null) {
            unidade.setAtivo(true);
        }
        return repository.save(unidade);
    }

    public List<Unidade> listarTodos() {
        return repository.findAll();
    }
}