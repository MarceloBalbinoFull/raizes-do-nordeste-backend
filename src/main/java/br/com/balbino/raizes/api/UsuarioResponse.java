
package br.com.balbino.raizes.api;

import br.com.balbino.raizes.domain.Usuario;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private Boolean ativo;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.perfil = usuario.getPerfil();
        this.ativo = usuario.getAtivo();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getPerfil() {
        return perfil;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}