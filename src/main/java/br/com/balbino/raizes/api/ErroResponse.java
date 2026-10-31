package br.com.balbino.raizes.api;

import java.util.Map;

public class ErroResponse {

    private int status;
    private String mensagem;
    private Map<String, String> erros;

    public ErroResponse(int status, String mensagem, Map<String, String> erros) {
        this.status = status;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, String> getErros() {
        return erros;
    }
}