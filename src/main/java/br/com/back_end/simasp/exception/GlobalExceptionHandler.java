package br.com.back_end.simasp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarRecursoNaoEncontrado(RecursoNaoEncontradoException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());

        problema.setTitle("Recurso procurado não encontrado.");
        problema.setProperty("codigo", "RECURSO_NAO_ENCONTRADO");

        return problema;
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ProblemDetail tratarCredenciaisInvalidas(CredenciaisInvalidasException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
        problema.setTitle("Falha na autenticação");
        problema.setProperty("codigo", "CREDENCIAIS_INVALIDAS");

        return problema;
    }

    @ExceptionHandler(LocalizacaoSensorDuplicadaException.class)
    public ProblemDetail tratarSensorComLocalizacaoDuplicada(LocalizacaoSensorDuplicadaException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());

        problema.setTitle("Sensor com latitude e longitude duplicado.");
        problema.setProperty("codigo", "SENSOR_COM_LOCALIZACAO_DUPLICADA");

        return problema;
    }


    @ExceptionHandler(AlteracaoInvalidaException.class)
    public ProblemDetail tratarRecursoRepetido(AlteracaoInvalidaException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problema.setTitle("Alteração inválida");
        problema.setProperty("codigo", "ALTERACAO_INVALIDA");

        return problema;
    }

    @ExceptionHandler(RecursoExistenteException.class)
    public ProblemDetail tratarRecursoExistente(RecursoExistenteException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problema.setTitle("Recurso existente");
        problema.setProperty("codigo", "RECURSO_EXISTENTE");

        return problema;
    }

}
