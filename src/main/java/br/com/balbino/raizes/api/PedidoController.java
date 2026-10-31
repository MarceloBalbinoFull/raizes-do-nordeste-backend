
package br.com.balbino.raizes.api;

import br.com.balbino.raizes.application.PedidoService;
import br.com.balbino.raizes.domain.Pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public PedidoResponse criar(@RequestBody @Valid PedidoRequest request) {
        return new PedidoResponse(pedidoService.criar(request));
    }

    @GetMapping
    public List<PedidoResponse> listarTodos(
            @RequestParam(required = false) String canalPedido) {

        List<Pedido> pedidos;

        if (canalPedido == null || canalPedido.isBlank()) {
            pedidos = pedidoService.listarTodos();
        } else {
            pedidos = pedidoService.listarPorCanal(canalPedido);
        }

        return pedidos.stream()
                .map(PedidoResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return new PedidoResponse(pedidoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public PedidoResponse atualizarStatus(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarStatusRequest request) {

        return new PedidoResponse(
                pedidoService.atualizarStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return new PedidoResponse(pedidoService.cancelar(id));
    }

    public static class AtualizarStatusRequest {

        @NotBlank(message = "O status é obrigatório")
        @Pattern(
                regexp = "EM_PREPARACAO|PRONTO|ENTREGUE",
                message = "Status permitido: EM_PREPARACAO, PRONTO ou ENTREGUE"
        )
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
