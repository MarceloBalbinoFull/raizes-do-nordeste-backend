package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.Fidelidade;

import java.time.LocalDateTime;

public class FidelidadeResponse {

    private Long id;
    private Long usuarioId;
    private Integer pontos;
    private Boolean consentimento;
    private LocalDateTime dataAtualizacao;

    public FidelidadeResponse(Fidelidade fidelidade) {
        this.id = fidelidade.getId();
        this.usuarioId = fidelidade.getUsuario().getId();
        this.pontos = fidelidade.getPontos();
        this.consentimento = fidelidade.getConsentimento();
        this.dataAtualizacao = fidelidade.getDataAtualizacao();
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Integer getPontos() {
        return pontos;
    }

    public Boolean getConsentimento() {
        return consentimento;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}