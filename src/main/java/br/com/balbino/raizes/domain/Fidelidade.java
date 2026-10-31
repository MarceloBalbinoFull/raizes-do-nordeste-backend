package br.com.balbino.raizes.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fidelidade",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fidelidade_usuario",
                        columnNames = "usuario_id"
                )
        }
)
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer pontos = 0;

    @Column(nullable = false)
    private Boolean consentimento;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @PrePersist
    @PreUpdate
    public void atualizarData() {
        dataAtualizacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public Boolean getConsentimento() {
        return consentimento;
    }

    public void setConsentimento(Boolean consentimento) {
        this.consentimento = consentimento;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}