package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.EstoqueService;
import br.com.balbino.raizes.domain.Estoque;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @PostMapping
    public EstoqueResponse salvar(@RequestBody @Valid EstoqueRequest request) {
        return new EstoqueResponse(estoqueService.salvar(request));
    }

    @GetMapping
    public List<EstoqueResponse> listarTodos() {
        return estoqueService.listarTodos().stream()
                .map(EstoqueResponse::new)
                .toList();
    }
}