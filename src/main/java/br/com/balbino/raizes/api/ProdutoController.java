
package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.ProdutoService;
import br.com.balbino.raizes.domain.Produto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public Produto criar(@RequestBody @Valid Produto produto) {
        return produtoService.salvar(produto);
    }

    @GetMapping
    public List<Produto> listarTodos(
            @RequestParam(required = false) Long unidadeId) {

        if (unidadeId != null) {
            return produtoService.listarDisponiveisPorUnidade(unidadeId);
        }

        return produtoService.listarTodos();
    }
}
