package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.FidelidadeService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FidelidadeResponse cadastrar(
            @Valid @RequestBody FidelidadeRequest request) {
        return fidelidadeService.cadastrar(request);
    }

    @GetMapping("/{usuarioId}")
    public FidelidadeResponse consultar(@PathVariable Long usuarioId) {
        return fidelidadeService.consultarPorUsuario(usuarioId);
    }

    @PatchMapping("/{usuarioId}/consentimento")
    public FidelidadeResponse atualizarConsentimento(
            @PathVariable Long usuarioId,
            @RequestBody ConsentimentoRequest request) {
        return fidelidadeService.atualizarConsentimento(
                usuarioId, request.getConsentimento());
    }

    @PostMapping("/{usuarioId}/pontos")
    public FidelidadeResponse adicionarPontos(
            @PathVariable Long usuarioId,
            @RequestBody PontosRequest request) {
        return fidelidadeService.adicionarPontos(
                usuarioId, request.getPontos());
    }

    @PostMapping("/{usuarioId}/resgates")
    public FidelidadeResponse resgatarPontos(
            @PathVariable Long usuarioId,
            @RequestBody PontosRequest request) {
        return fidelidadeService.resgatarPontos(
                usuarioId, request.getPontos());
    }

    public static class ConsentimentoRequest {

        private Boolean consentimento;

        public Boolean getConsentimento() {
            return consentimento;
        }

        public void setConsentimento(Boolean consentimento) {
            this.consentimento = consentimento;
        }
    }

    public static class PontosRequest {

        private Integer pontos;

        public Integer getPontos() {
            return pontos;
        }

        public void setPontos(Integer pontos) {
            this.pontos = pontos;
        }
    }
}