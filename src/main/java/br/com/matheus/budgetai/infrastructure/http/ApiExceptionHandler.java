package br.com.matheus.budgetai.infrastructure.http;

import br.com.matheus.budgetai.infrastructure.ai.AiProviderException;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
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
        log.warn("Falha na integração de IA: code={}, providerStatus={}", ex.getDiagnosticCode(), ex.getProviderStatus());
        String explanation = switch (ex.getDiagnosticCode()) {
            case "INSUFFICIENT_CREDITS" -> "Verifique créditos e cota no Billing da API OpenAI.";
            case "AUTHENTICATION_FAILED" -> "A chave da API foi recusada. Confira se está correta e ativa.";
            case "PERMISSION_DENIED" -> "A chave ou o projeto não tem permissão para esta operação ou modelo.";
            case "RATE_LIMITED" -> "O provedor limitou as chamadas. Verifique a cota e tente novamente mais tarde.";
            case "MODEL_UNAVAILABLE" -> "O modelo configurado não está disponível para este projeto.";
            case "DNS_ERROR" -> "Não foi possível resolver o endereço do provedor. Confira a conexão e o DNS.";
            case "NETWORK_ERROR" -> "Não foi possível conectar ao provedor. Confira a conexão de rede.";
            default -> "Não foi possível concluir a etapa de IA.";
        };
        var body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, explanation +
                " Antes de repetir uma criação, consulte as despesas: o registro pode já ter sido salvo.");
        body.setTitle("Falha na integração de IA");
        body.setProperty("code", ex.getDiagnosticCode());
        if (ex.getProviderStatus() != null) body.setProperty("providerStatus", ex.getProviderStatus());
        return ResponseEntity.status(502).body(body);
    }
    private ResponseEntity<ProblemDetail> problem(int status, String detail) {
        var body = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), detail);
        body.setTitle(status == 502 ? "Falha na integração de IA" : "Requisição inválida");
        return ResponseEntity.status(status).body(body);
    }
}
