package br.com.balbino.raizes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"produto_id", "unidade_id"}
        )
)
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    @Column(nullable = false)
    private Integer quantidade;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    @PreUpdate
    public void atualizarData() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public Unidade getUnidade() { return unidade; }
    public Integer getQuantidade() { return quantidade; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    public void setProduto(Produto produto) { this.produto = produto; }
    public void setUnidade(Unidade unidade) { this.unidade = unidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
