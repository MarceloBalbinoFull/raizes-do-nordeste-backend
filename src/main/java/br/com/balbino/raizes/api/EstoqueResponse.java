package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.Estoque;
import java.time.LocalDateTime;

public class EstoqueResponse {

    private final Long id;
    private final Long produtoId;
    private final String produtoNome;
    private final Long unidadeId;
    private final String unidadeNome;
    private final Integer quantidade;
    private final LocalDateTime dataAtualizacao;

    public EstoqueResponse(Estoque estoque) {
        this.id = estoque.getId();
        this.produtoId = estoque.getProduto().getId();
        this.produtoNome = estoque.getProduto().getNome();
        this.unidadeId = estoque.getUnidade().getId();
        this.unidadeNome = estoque.getUnidade().getNome();
        this.quantidade = estoque.getQuantidade();
        this.dataAtualizacao = estoque.getDataAtualizacao();
    }

    public Long getId() { return id; }
    public Long getProdutoId() { return produtoId; }
    public String getProdutoNome() { return produtoNome; }
    public Long getUnidadeId() { return unidadeId; }
    public String getUnidadeNome() { return unidadeNome; }
    public Integer getQuantidade() { return quantidade; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}