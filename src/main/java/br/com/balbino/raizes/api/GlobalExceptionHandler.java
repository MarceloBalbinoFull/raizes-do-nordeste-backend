
package br.com.balbino.raizes.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse tratarValidacao(
            MethodArgumentNotValidException exception) {

        Map<String, String> erros = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        erros.put(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

        return new ErroResponse(
                400,
                "Dados inválidos",
                erros
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse tratarNaoEncontrado(
            IllegalArgumentException exception) {

        return new ErroResponse(
                404,
                exception.getMessage(),
                null
        );
    }

    @ExceptionHandler({
            IllegalStateException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse tratarConflito(Exception exception) {

        return new ErroResponse(
                409,
                exception.getMessage() != null
                        ? exception.getMessage()
                        : "Conflito com uma regra de negócio",
                null
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResponse> tratarResponseStatus(
            ResponseStatusException exception) {

        int codigo = exception.getStatusCode().value();

        String mensagem = exception.getReason() != null
                ? exception.getReason()
                : "Erro na requisição";

        ErroResponse erro = new ErroResponse(
                codigo,
                mensagem,
                null
        );

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(erro);
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse tratarErroNumerico(
            ArithmeticException exception) {

        return new ErroResponse(
                400,
                "Valor numérico inválido ou fora do limite permitido",
                null
        );
    }
}
