
package br.com.balbino.raizes.api;

public class LoginResponse {

    private final String token;
    private final String tipo = "Bearer";
    private final Long usuarioId;
    private final String perfil;

    public LoginResponse(
            String token,
            Long usuarioId,
            String perfil) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getPerfil() {
        return perfil;
    }
}
