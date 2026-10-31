package br.com.balbino.raizes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(nullable = false)
    private String canalPedido;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    private LocalDateTime dataCriacao;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemPedido> itens = new ArrayList<>();

    @PrePersist
    public void prepararCadastro() {
        dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Unidade getUnidade() { return unidade; }
    public String getCanalPedido() { return canalPedido; }
    public String getStatus() { return status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<ItemPedido> getItens() { return itens; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public void setCanalPedido(String canalPedido) {
        this.canalPedido = canalPedido;
    }
    public void setStatus(String status) { this.status = status; }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}