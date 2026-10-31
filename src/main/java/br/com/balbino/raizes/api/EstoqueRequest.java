package br.com.balbino.raizes.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EstoqueRequest {

    @NotNull
    private Long produtoId;

    @NotNull
    private Long unidadeId;

    @NotNull
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer quantidade;

    public Long getProdutoId() { return produtoId; }
    public Long getUnidadeId() { return unidadeId; }
    public Integer getQuantidade() { return quantidade; }
}