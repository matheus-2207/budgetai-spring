package br.com.matheus.budgetai;

import br.com.matheus.budgetai.infrastructure.ai.AiProviderException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import java.net.UnknownHostException;
import static org.assertj.core.api.Assertions.*;

class AiProviderExceptionTest {
    @Test void distinguishesQuotaFromRateLimiting() {
        var quota = new AiProviderException(new IllegalStateException("429 - {\"error\":{\"code\":\"insufficient_quota\"}}"));
        assertThat(quota.getDiagnosticCode()).isEqualTo("INSUFFICIENT_CREDITS");
        assertThat(quota.getProviderStatus()).isEqualTo(429);
        var rate = new AiProviderException(new IllegalStateException("429 - rate limited"));
        assertThat(rate.getDiagnosticCode()).isEqualTo("RATE_LIMITED");
    }
    @Test void findsAuthenticationFailureInNestedCause() {
        var failure = new AiProviderException(new IllegalStateException(HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED, "unauthorized", null, new byte[0], null)));
        assertThat(failure.getDiagnosticCode()).isEqualTo("AUTHENTICATION_FAILED");
        assertThat(failure.getProviderStatus()).isEqualTo(401);
    }
    @Test void findsDnsFailure() {
        var failure = new AiProviderException(new IllegalStateException(new UnknownHostException("api.openai.com")));
        assertThat(failure.getDiagnosticCode()).isEqualTo("DNS_ERROR");
    }
    @Test void distinguishesScopeFailureWithoutReturningRawMessage() {
        var failure = new AiProviderException(new IllegalStateException("403 - Missing scopes: model.request; sk-fake-secret"));
        assertThat(failure.getDiagnosticCode()).isEqualTo("PERMISSION_DENIED");
        assertThat(failure.getMessage()).doesNotContain("sk-fake-secret", "Missing scopes");
    }
}
