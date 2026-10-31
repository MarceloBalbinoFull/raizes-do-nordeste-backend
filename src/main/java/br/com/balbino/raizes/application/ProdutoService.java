
package br.com.balbino.raizes.application;

import br.com.balbino.raizes.domain.Produto;
import br.com.balbino.raizes.domain.Unidade;
import br.com.balbino.raizes.repository.EstoqueRepository;
import br.com.balbino.raizes.repository.ProdutoRepository;
import br.com.balbino.raizes.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;
    private final EstoqueRepository estoqueRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository,
            EstoqueRepository estoqueRepository) {

        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
        this.estoqueRepository = estoqueRepository;
    }

    public Produto salvar(Produto produto) {
        if (produto.getAtivo() == null) {
            produto.setAtivo(true);
        }

        return produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarDisponiveisPorUnidade(Long unidadeId) {

        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Unidade nao encontrada"));

        if (!Boolean.TRUE.equals(unidade.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unidade inativa");
        }

        return estoqueRepository.findProdutosDisponiveisPorUnidade(unidadeId);
    }
}
