package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.ItemPedido;
import br.com.balbino.raizes.domain.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private final Long id;
    private final Long usuarioId;
    private final Long unidadeId;
    private final String canalPedido;
    private final String status;
    private final BigDecimal valorTotal;
    private final LocalDateTime dataCriacao;
    private final List<ItemResponse> itens;

    public PedidoResponse(Pedido pedido) {
        this.id = pedido.getId();
        this.usuarioId = pedido.getUsuario().getId();
        this.unidadeId = pedido.getUnidade().getId();
        this.canalPedido = pedido.getCanalPedido();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
        this.dataCriacao = pedido.getDataCriacao();
        this.itens = pedido.getItens().stream()
                .map(ItemResponse::new)
                .toList();
    }

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public Long getUnidadeId() { return unidadeId; }
    public String getCanalPedido() { return canalPedido; }
    public String getStatus() { return status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<ItemResponse> getItens() { return itens; }

    public static class ItemResponse {
        private final Long produtoId;
        private final String produtoNome;
        private final Integer quantidade;
        private final BigDecimal valorUnitario;
        private final BigDecimal valorTotal;

        public ItemResponse(ItemPedido item) {
            this.produtoId = item.getProduto().getId();
            this.produtoNome = item.getProduto().getNome();
            this.quantidade = item.getQuantidade();
            this.valorUnitario = item.getValorUnitario();
            this.valorTotal = item.getValorTotal();
        }

        public Long getProdutoId() { return produtoId; }
        public String getProdutoNome() { return produtoNome; }
        public Integer getQuantidade() { return quantidade; }
        public BigDecimal getValorUnitario() { return valorUnitario; }
        public BigDecimal getValorTotal() { return valorTotal; }
    }
}