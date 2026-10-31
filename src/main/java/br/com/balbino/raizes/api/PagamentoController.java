package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public PagamentoResponse processar(
            @RequestBody @Valid PagamentoRequest request) {
        return new PagamentoResponse(
                pagamentoService.processar(request));
    }

    @GetMapping
    public List<PagamentoResponse> listarTodos() {
        return pagamentoService.listarTodos().stream()
                .map(PagamentoResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscarPorId(@PathVariable Long id) {
        return new PagamentoResponse(
                pagamentoService.buscarPorId(id));
    }
}