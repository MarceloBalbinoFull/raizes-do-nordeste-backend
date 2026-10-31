package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.PagamentoRequest;
import br.com.balbino.raizes.domain.Estoque;
import br.com.balbino.raizes.domain.Pagamento;
import br.com.balbino.raizes.domain.Pedido;
import br.com.balbino.raizes.domain.ItemPedido;
import br.com.balbino.raizes.repository.EstoqueRepository;
import br.com.balbino.raizes.repository.PagamentoRepository;
import br.com.balbino.raizes.repository.PedidoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final EstoqueRepository estoqueRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            EstoqueRepository estoqueRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional
    public Pagamento processar(PagamentoRequest request) {

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pedido não encontrado"));

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O pedido não está aguardando pagamento");
        }

        if (pagamentoRepository.existsByPedido_Id(pedido.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe pagamento para este pedido");
        }

        String resultado = request.getResultadoSimulado();

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setStatus(resultado);

        if ("APROVADO".equals(resultado)) {
            pedido.setStatus("CONFIRMADO");
        } else {
            pedido.setStatus("PAGAMENTO_RECUSADO");
            devolverEstoque(pedido);
        }

        pedidoRepository.save(pedido);
        return pagamentoRepository.save(pagamento);
    }

    private void devolverEstoque(Pedido pedido) {

        List<ItemPedido> itens = pedido.getItens();

        for (ItemPedido item : itens) {
            Estoque estoque = estoqueRepository
                    .findByProduto_IdAndUnidade_Id(
                            item.getProduto().getId(),
                            pedido.getUnidade().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Estoque não encontrado para devolução"));

            estoque.setQuantidade(
                    estoque.getQuantidade() + item.getQuantidade());

            estoqueRepository.save(estoque);
        }
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento não encontrado"));
    }
}