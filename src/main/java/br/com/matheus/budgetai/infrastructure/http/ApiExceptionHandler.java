package br.com.matheus.budgetai.infrastructure.http;

import br.com.matheus.budgetai.infrastructure.ai.AiProviderException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> invalid(IllegalArgumentException ex) { return problem(400, ex.getMessage()); }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, MissingServletRequestPartException.class,
            MethodArgumentNotValidException.class})
    ResponseEntity<ProblemDetail> malformed(Exception ex) {
        return problem(400, "Entrada inválida. Confira os campos, datas ISO (AAAA-MM-DD) e categorias.");
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<ProblemDetail> large(MaxUploadSizeExceededException ex) { return problem(413, "Áudio deve ter no máximo 10 MB."); }
    @ExceptionHandler(AiProviderException.class)
    ResponseEntity<ProblemDetail> provider(AiProviderException ex) {
        return problem(502, "Não foi possível concluir a etapa de IA. Antes de repetir um comando de criação, consulte as despesas: o registro pode já ter sido salvo.");
    }
    private ResponseEntity<ProblemDetail> problem(int status, String detail) {
        var body = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), detail);
        body.setTitle(status == 502 ? "Falha na integração de IA" : "Requisição inválida");
        return ResponseEntity.status(status).body(body);
    }
}
