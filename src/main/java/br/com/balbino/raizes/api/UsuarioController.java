package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.UsuarioService;
import br.com.balbino.raizes.domain.Usuario;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public UsuarioResponse salvar(
            @RequestBody @Valid UsuarioRequest request) {
        Usuario usuarioSalvo = usuarioService.salvar(request);
        return new UsuarioResponse(usuarioSalvo);
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponse::new)
                .toList();
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
    }
}