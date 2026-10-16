package br.com.matheus.budgetai.infrastructure.ai;

import org.springframework.web.client.RestClientResponseException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Pattern;

/** Classifies failures without exposing provider bodies, prompts or credentials. */
public class AiProviderException extends RuntimeException {
    private static final Pattern STATUS = Pattern.compile("^(?:HTTP\\s*)?([45]\\d{2})\\b");
    private final String diagnosticCode;
    private final Integer providerStatus;

    public AiProviderException(RuntimeException cause) {
        super("Falha na integração de IA", cause);
        Integer status = null;
        String reason = "PROVIDER_ERROR";
        Throwable current = cause;
        for (int i = 0; current != null && i < 20; i++, current = current.getCause()) {
            if (current instanceof RestClientResponseException response) status = response.getStatusCode().value();
            String message = current.getMessage() == null ? "" : current.getMessage();
            var match = STATUS.matcher(message);
            if (status == null && match.find()) status = Integer.parseInt(match.group(1));
            String lower = message.toLowerCase(Locale.ROOT);
            if (lower.contains("insufficient_quota")) reason = "INSUFFICIENT_CREDITS";
            else if (lower.contains("missing scopes") || lower.contains("insufficient_permissions")) reason = "PERMISSION_DENIED";
            else if (lower.contains("model_not_found")) reason = "MODEL_UNAVAILABLE";
            if (current instanceof UnknownHostException) reason = "DNS_ERROR";
            else if (current instanceof ConnectException) reason = "NETWORK_ERROR";
        }
        if (reason.equals("PROVIDER_ERROR") && status != null) {
            reason = switch (status) {
                case 401 -> "AUTHENTICATION_FAILED";
                case 403 -> "PERMISSION_DENIED";
                case 429 -> "RATE_LIMITED";
                default -> "PROVIDER_ERROR";
            };
        }
        this.providerStatus = status;
        this.diagnosticCode = reason;
    }
    public String getDiagnosticCode() { return diagnosticCode; }
    public Integer getProviderStatus() { return providerStatus; }
}
