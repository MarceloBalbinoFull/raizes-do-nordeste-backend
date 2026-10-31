
package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.PromocaoService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promocoes")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(
            PromocaoService promocaoService) {
        this.promocaoService = promocaoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromocaoResponse criar(
            @RequestBody @Valid PromocaoRequest request) {

        return new PromocaoResponse(
                promocaoService.criar(request));
    }

    @GetMapping
    public List<PromocaoResponse> listarTodos() {

        return promocaoService.listarTodos()
                .stream()
                .map(PromocaoResponse::new)
                .toList();
    }

    @GetMapping("/ativas")
    public List<PromocaoResponse> listarAtivas() {

        return promocaoService.listarAtivas()
                .stream()
                .map(PromocaoResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PromocaoResponse buscarPorId(
            @PathVariable Long id) {

        return new PromocaoResponse(
                promocaoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/desativar")
    public PromocaoResponse desativar(
            @PathVariable Long id) {

        return new PromocaoResponse(
                promocaoService.desativar(id));
    }
}
