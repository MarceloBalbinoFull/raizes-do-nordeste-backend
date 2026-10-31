package br.com.balbino.raizes.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemPedidoRequest {

    @NotNull
    private Long produtoId;

    @NotNull
    @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
    private Integer quantidade;

    public Long getProdutoId() { return produtoId; }
    public Integer getQuantidade() { return quantidade; }
}
