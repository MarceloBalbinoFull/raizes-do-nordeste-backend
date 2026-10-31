package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.EstoqueRequest;
import br.com.balbino.raizes.domain.Estoque;
import br.com.balbino.raizes.domain.Produto;
import br.com.balbino.raizes.domain.Unidade;
import br.com.balbino.raizes.repository.EstoqueRepository;
import br.com.balbino.raizes.repository.ProdutoRepository;
import br.com.balbino.raizes.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueService(
            EstoqueRepository estoqueRepository,
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public Estoque salvar(EstoqueRequest request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produto não encontrado"));

        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        Estoque estoque = estoqueRepository.findAll().stream()
                .filter(e -> e.getProduto().getId().equals(request.getProdutoId())
                        && e.getUnidade().getId().equals(request.getUnidadeId()))
                .findFirst()
                .orElseGet(Estoque::new);

        estoque.setProduto(produto);
        estoque.setUnidade(unidade);
        estoque.setQuantidade(request.getQuantidade());

        return estoqueRepository.save(estoque);
    }

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }
}
