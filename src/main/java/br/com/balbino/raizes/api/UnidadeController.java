package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.UnidadeService;
import br.com.balbino.raizes.domain.Unidade;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @PostMapping
    public Unidade criar(@RequestBody @Valid Unidade unidade) {
        return unidadeService.salvar(unidade);
    }

    @GetMapping
    public List<Unidade> listarTodos() {
        return unidadeService.listarTodos();
    }
}