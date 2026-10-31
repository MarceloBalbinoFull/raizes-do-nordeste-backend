
package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.ItemPedidoRequest;
import br.com.balbino.raizes.api.PedidoRequest;
import br.com.balbino.raizes.domain.*;
import br.com.balbino.raizes.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            UnidadeRepository unidadeRepository,
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    // Identifica o usuario autenticado pelo JWT.
    private Authentication obterAutenticacao() {

        Authentication autenticacao =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (autenticacao == null
                || !autenticacao.isAuthenticated()) {

            throw new AccessDeniedException(
                    "Usuario nao autenticado");
        }

        return autenticacao;
    }

    private boolean ehCliente() {

        return obterAutenticacao()
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "CLIENTE".equals(
                                authority.getAuthority()));
    }

    private Long obterUsuarioAutenticadoId() {

        Object principal =
                obterAutenticacao().getPrincipal();

        if (!(principal instanceof Long)) {
            throw new AccessDeniedException(
                    "Identidade do usuario invalida");
        }

        return (Long) principal;
    }

    private void validarProprietario(Pedido pedido) {

        if (ehCliente()
                && !Objects.equals(
                pedido.getUsuario().getId(),
                obterUsuarioAutenticadoId())) {

            throw new AccessDeniedException(
                    "Voce nao pode acessar este pedido");
        }
    }

    @Transactional
    public Pedido criar(PedidoRequest request) {

        // CLIENTE somente cria pedidos para si mesmo.
        if (ehCliente()
                && !Objects.equals(
                request.getUsuarioId(),
                obterUsuarioAutenticadoId())) {

            throw new AccessDeniedException(
                    "Cliente so pode criar pedidos para si");
        }

        Usuario usuario = usuarioRepository
                .findById(request.getUsuarioId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario nao encontrado"));

        Unidade unidade = unidadeRepository
                .findById(request.getUnidadeId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Unidade nao encontrada"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Usuario inativo");
        }

        if (!Boolean.TRUE.equals(unidade.getAtivo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unidade inativa");
        }

        Map<Long, Integer> quantidades =
                new LinkedHashMap<>();

        for (ItemPedidoRequest item : request.getItens()) {

            if (item.getProdutoId() == null
                    || item.getQuantidade() == null
                    || item.getQuantidade() < 1) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Item do pedido invalido");
            }

            quantidades.merge(
                    item.getProdutoId(),
                    item.getQuantidade(),
                    Integer::sum);
        }

        Map<Long, Produto> produtos =
                new LinkedHashMap<>();

        Map<Long, Estoque> estoques =
                new LinkedHashMap<>();

        for (Map.Entry<Long, Integer> entrada
                : quantidades.entrySet()) {

            Long produtoId = entrada.getKey();
            Integer quantidade = entrada.getValue();

            Produto produto = produtoRepository
                    .findById(produtoId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Produto nao encontrado: "
                                            + produtoId));

            if (!Boolean.TRUE.equals(produto.getAtivo())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Produto inativo: " + produtoId);
            }

            Estoque estoque = estoqueRepository
                    .findByProduto_IdAndUnidade_Id(
                            produtoId,
                            unidade.getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.CONFLICT,
                                    "Nao existe estoque para o produto "
                                            + produtoId
                                            + " nesta unidade"));

            if (estoque.getQuantidade() < quantidade) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Estoque insuficiente para o produto "
                                + produtoId);
            }

            produtos.put(produtoId, produto);
            estoques.put(produtoId, estoque);
        }

        Pedido pedido = new Pedido();

        pedido.setUsuario(usuario);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(request.getCanalPedido());
        pedido.setStatus("AGUARDANDO_PAGAMENTO");

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entrada
                : quantidades.entrySet()) {

            Long produtoId = entrada.getKey();
            Integer quantidade = entrada.getValue();

            Produto produto = produtos.get(produtoId);
            Estoque estoque = estoques.get(produtoId);

            BigDecimal subtotal = produto.getPreco()
                    .multiply(BigDecimal.valueOf(quantidade));

            ItemPedido item = new ItemPedido();

            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setValorUnitario(produto.getPreco());
            item.setValorTotal(subtotal);

            pedido.adicionarItem(item);

            total = total.add(subtotal);

            estoque.setQuantidade(
                    estoque.getQuantidade() - quantidade);

            estoqueRepository.save(estoque);
        }

        pedido.setValorTotal(total);

        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {

        if (ehCliente()) {
            return pedidoRepository.findByUsuario_Id(
                    obterUsuarioAutenticadoId());
        }

        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCanal(String canalPedido) {

        String canal = canalPedido
                .trim()
                .toUpperCase(java.util.Locale.ROOT);

        if (!List.of(
                "APP", "TOTEM", "BALCAO", "PICKUP", "WEB"
        ).contains(canal)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Canal invalido. Utilize APP, TOTEM, "
                            + "BALCAO, PICKUP ou WEB");
        }

        if (ehCliente()) {
            return pedidoRepository
                    .findByUsuario_IdAndCanalPedidoIgnoreCase(
                            obterUsuarioAutenticadoId(),
                            canal);
        }

        return pedidoRepository
                .findByCanalPedidoIgnoreCase(canal);
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Pedido nao encontrado"));

        validarProprietario(pedido);

        return pedido;
    }

    @Transactional
    public Pedido atualizarStatus(
            Long id,
            String novoStatus) {

        Pedido pedido = buscarPorId(id);
        String statusAtual = pedido.getStatus();

        boolean transicaoPermitida =
                ("CONFIRMADO".equals(statusAtual)
                        && "EM_PREPARACAO".equals(novoStatus))
                        || ("EM_PREPARACAO".equals(statusAtual)
                        && "PRONTO".equals(novoStatus))
                        || ("PRONTO".equals(statusAtual)
                        && "ENTREGUE".equals(novoStatus));

        if (!transicaoPermitida) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transicao de status nao permitida: "
                            + statusAtual
                            + " para "
                            + novoStatus);
        }

        pedido.setStatus(novoStatus);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelar(Long id) {

        Pedido pedido = buscarPorId(id);

        if (!"AGUARDANDO_PAGAMENTO"
                .equals(pedido.getStatus())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Somente pedidos aguardando pagamento "
                            + "podem ser cancelados");
        }

        devolverEstoque(pedido);

        pedido.setStatus("CANCELADO");

        return pedidoRepository.save(pedido);
    }

    private void devolverEstoque(Pedido pedido) {

        for (ItemPedido item : pedido.getItens()) {

            Estoque estoque = estoqueRepository
                    .findByProduto_IdAndUnidade_Id(
                            item.getProduto().getId(),
                            pedido.getUnidade().getId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.CONFLICT,
                                    "Estoque nao encontrado "
                                            + "para devolucao"));

            estoque.setQuantidade(
                    estoque.getQuantidade()
                            + item.getQuantidade());

            estoqueRepository.save(estoque);
        }
    }
}
