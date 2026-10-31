
package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.PromocaoRequest;
import br.com.balbino.raizes.domain.Produto;
import br.com.balbino.raizes.domain.Promocao;
import br.com.balbino.raizes.repository.ProdutoRepository;
import br.com.balbino.raizes.repository.PromocaoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProdutoRepository produtoRepository;

    public PromocaoService(
            PromocaoRepository promocaoRepository,
            ProdutoRepository produtoRepository) {
        this.promocaoRepository = promocaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Promocao criar(PromocaoRequest request) {

        if (request.getDataFim()
                .isBefore(request.getDataInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à inicial");
        }

        Produto produto = produtoRepository
                .findById(request.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado"));

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não é permitido criar promoção para produto inativo");
        }

        Promocao promocao = new Promocao();
        promocao.setNome(request.getNome().trim());
        promocao.setProduto(produto);
        promocao.setPercentualDesconto(
                request.getPercentualDesconto());
        promocao.setDataInicio(request.getDataInicio());
        promocao.setDataFim(request.getDataFim());
        promocao.setAtivo(true);

        return promocaoRepository.save(promocao);
    }

    @Transactional(readOnly = true)
    public List<Promocao> listarTodos() {
        return promocaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Promocao buscarPorId(Long id) {
        return promocaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Promoção não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Promocao> listarAtivas() {

        LocalDate hoje = LocalDate.now();

        return promocaoRepository
                .findByAtivoTrueAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        hoje, hoje)
                .stream()
                .filter(promocao ->
                        Boolean.TRUE.equals(
                                promocao.getProduto().getAtivo()))
                .toList();
    }

    @Transactional
    public Promocao desativar(Long id) {

        Promocao promocao = buscarPorId(id);

        if (!Boolean.TRUE.equals(promocao.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A promoção já está desativada");
        }

        promocao.setAtivo(false);

        return promocaoRepository.save(promocao);
    }
}
