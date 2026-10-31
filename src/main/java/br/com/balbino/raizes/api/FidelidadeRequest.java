package br.com.balbino.raizes.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FidelidadeRequest {

    @NotNull(message = "O ID do usuário é obrigatório")
    @Positive(message = "O ID do usuário deve ser positivo")
    private Long usuarioId;

    @NotNull(message = "O consentimento é obrigatório")
    private Boolean consentimento;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Boolean getConsentimento() {
        return consentimento;
    }

    public void setConsentimento(Boolean consentimento) {
        this.consentimento = consentimento;
    }
}