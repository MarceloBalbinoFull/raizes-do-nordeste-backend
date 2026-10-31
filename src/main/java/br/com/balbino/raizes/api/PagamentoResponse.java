package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.Pagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponse {

    private final Long id;
    private final Long pedidoId;
    private final String status;
    private final BigDecimal valor;
    private final LocalDateTime dataProcessamento;

    public PagamentoResponse(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.pedidoId = pagamento.getPedido().getId();
        this.status = pagamento.getStatus();
        this.valor = pagamento.getValor();
        this.dataProcessamento = pagamento.getDataProcessamento();
    }

    public Long getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }
}