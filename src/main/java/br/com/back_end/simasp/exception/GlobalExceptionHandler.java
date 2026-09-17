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

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ProblemDetail tratarEmailJaCadastrado(EmailJaCadastradoException exception)
    {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());

        problema.setTitle("E-mail já cadastrado.");
        problema.setProperty("codigo", "EMAIL_JA_CADASTRADO");

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

}
