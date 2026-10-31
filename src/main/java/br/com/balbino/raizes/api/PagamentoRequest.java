package br.com.balbino.raizes.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PagamentoRequest {

    @NotNull
    private Long pedidoId;

    @NotNull
    @Pattern(
            regexp = "APROVADO|RECUSADO",
            message = "O resultado deve ser APROVADO ou RECUSADO"
    )
    private String resultadoSimulado;

    public Long getPedidoId() {
        return pedidoId;
    }

    public String getResultadoSimulado() {
        return resultadoSimulado;
    }
}