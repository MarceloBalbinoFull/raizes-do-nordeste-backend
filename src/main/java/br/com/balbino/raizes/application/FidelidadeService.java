package br.com.balbino.raizes.application;

import br.com.balbino.raizes.api.FidelidadeRequest;
import br.com.balbino.raizes.api.FidelidadeResponse;
import br.com.balbino.raizes.domain.Fidelidade;
import br.com.balbino.raizes.domain.Usuario;
import br.com.balbino.raizes.repository.FidelidadeRepository;
import br.com.balbino.raizes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public FidelidadeService(
            FidelidadeRepository fidelidadeRepository,
            UsuarioRepository usuarioRepository) {
        this.fidelidadeRepository = fidelidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Authentication autenticacao() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuario nao autenticado");
        }
        return auth;
    }

    private boolean ehCliente() {
        return autenticacao().getAuthorities().stream()
                .anyMatch(a -> "CLIENTE".equals(a.getAuthority()));
    }

    private void validarPropriedade(Long usuarioId) {
        if (ehCliente()) {
            Object principal = autenticacao().getPrincipal();
            if (!(principal instanceof Long)
                    || !Objects.equals(usuarioId, principal)) {
                throw new AccessDeniedException(
                        "Cliente so pode acessar a propria fidelidade");
            }
        }
    }

    private Fidelidade obterCadastro(Long usuarioId) {
        return fidelidadeRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cadastro de fidelidade nao encontrado"));
    }

    private void validarPontos(Integer pontos) {
        if (pontos == null || pontos <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade de pontos deve ser maior que zero");
        }
    }

    @Transactional
    public FidelidadeResponse cadastrar(FidelidadeRequest request) {
        validarPropriedade(request.getUsuarioId());

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        if (fidelidadeRepository.existsByUsuario_Id(usuario.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O usuario ja possui cadastro de fidelidade");
        }

        Fidelidade fidelidade = new Fidelidade();
        fidelidade.setUsuario(usuario);
        fidelidade.setPontos(0);
        fidelidade.setConsentimento(request.getConsentimento());

        return new FidelidadeResponse(fidelidadeRepository.save(fidelidade));
    }

    @Transactional(readOnly = true)
    public FidelidadeResponse consultarPorUsuario(Long usuarioId) {
        validarPropriedade(usuarioId);
        return new FidelidadeResponse(obterCadastro(usuarioId));
    }

    @Transactional
    public FidelidadeResponse atualizarConsentimento(
            Long usuarioId, Boolean consentimento) {
        validarPropriedade(usuarioId);

        if (consentimento == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Consentimento e obrigatorio");
        }

        Fidelidade fidelidade = obterCadastro(usuarioId);
        fidelidade.setConsentimento(consentimento);

        // Mantem a regra de negocio ja existente no projeto.
        if (Boolean.FALSE.equals(consentimento)) {
            fidelidade.setPontos(0);
        }

        return new FidelidadeResponse(fidelidadeRepository.save(fidelidade));
    }

    @Transactional
    public FidelidadeResponse adicionarPontos(Long usuarioId, Integer pontos) {
        // Defesa adicional: nao depende apenas da rota estar protegida.
        if (ehCliente()) {
            throw new AccessDeniedException(
                    "Cliente nao pode creditar pontos");
        }
        validarPontos(pontos);

        Fidelidade fidelidade = obterCadastro(usuarioId);
        if (!Boolean.TRUE.equals(fidelidade.getConsentimento())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O usuario nao consentiu com o programa de fidelidade");
        }

        fidelidade.setPontos(Math.addExact(fidelidade.getPontos(), pontos));
        return new FidelidadeResponse(fidelidadeRepository.save(fidelidade));
    }

    @Transactional
    public FidelidadeResponse resgatarPontos(Long usuarioId, Integer pontos) {
        validarPropriedade(usuarioId);
        validarPontos(pontos);

        Fidelidade fidelidade = obterCadastro(usuarioId);
        if (!Boolean.TRUE.equals(fidelidade.getConsentimento())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "O usuario nao consentiu com o programa de fidelidade");
        }
        if (fidelidade.getPontos() < pontos) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Saldo de pontos insuficiente");
        }

        fidelidade.setPontos(fidelidade.getPontos() - pontos);
        return new FidelidadeResponse(fidelidadeRepository.save(fidelidade));
    }
}
