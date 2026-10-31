package br.com.balbino.raizes.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    private LocalDateTime dataProcessamento;

    @PrePersist
    public void prepararCadastro() {
        dataProcessamento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataProcessamento() {
        return dataProcessamento;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}