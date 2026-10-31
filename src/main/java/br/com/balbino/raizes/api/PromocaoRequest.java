
package br.com.balbino.raizes.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PromocaoRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotNull(message = "O produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "O desconto é obrigatório")
    @DecimalMin(value = "0.01",
            message = "O desconto deve ser maior que zero")
    @DecimalMax(value = "100.00",
            message = "O desconto não pode ultrapassar 100%")
    private BigDecimal percentualDesconto;

    @NotNull(message = "A data inicial é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "A data final é obrigatória")
    private LocalDate dataFim;

    public String getNome() {
        return nome;
    }

    public Long getProdutoId() {
        return produtoId;
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

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
}
