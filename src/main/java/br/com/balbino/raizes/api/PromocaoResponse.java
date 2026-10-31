
package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.Promocao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromocaoResponse {

    private final Long id;
    private final String nome;
    private final Long produtoId;
    private final String produtoNome;
    private final BigDecimal percentualDesconto;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private final Boolean ativo;

    public PromocaoResponse(Promocao promocao) {
        this.id = promocao.getId();
        this.nome = promocao.getNome();
        this.produtoId = promocao.getProduto().getId();
        this.produtoNome = promocao.getProduto().getNome();
        this.percentualDesconto =
                promocao.getPercentualDesconto();
        this.dataInicio = promocao.getDataInicio();
        this.dataFim = promocao.getDataFim();
        this.ativo = promocao.getAtivo();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
