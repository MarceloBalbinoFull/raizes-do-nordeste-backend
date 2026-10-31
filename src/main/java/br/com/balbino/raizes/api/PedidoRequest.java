package br.com.balbino.raizes.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class PedidoRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long unidadeId;

    @NotBlank
    @Pattern(
            regexp = "APP|TOTEM|BALCAO|PICKUP|WEB",
            message = "Canal inválido"
    )
    private String canalPedido;

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> itens;

    public Long getUsuarioId() { return usuarioId; }
    public Long getUnidadeId() { return unidadeId; }
    public String getCanalPedido() { return canalPedido; }
    public List<ItemPedidoRequest> getItens() { return itens; }
}