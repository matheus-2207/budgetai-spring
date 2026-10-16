package br.com.matheus.budgetai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.net.URI;
import java.net.http.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=jdbc:h2:mem:budgetai-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.profiles.active=test"})
class ExpenseApiTest {
    @LocalServerPort int port;
    private final HttpClient client = HttpClient.newHttpClient();
    private HttpResponse<String> post(String body) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/expenses"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
    private HttpResponse<String> get(String path) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }
    @Test void createsFiltersAndSummarizesInclusivePeriodWithExactTotals() throws Exception {
        assertThat(post("""
            {"description":"Mercado", "amount":35.90,"category":"GROCERIES","occurredOn":"2025-01-01"}
            """).statusCode()).isEqualTo(201);
        assertThat(post("""
            {"description":"Farmácia", "amount":14.10,"category":"PHARMA","occurredOn":"2025-01-31"}
            """).statusCode()).isEqualTo(201);
        assertThat(post("""
            {"description":"Fora do período", "amount":100,"category":"FOOD","occurredOn":"2025-02-01"}
            """).statusCode()).isEqualTo(201);
        var summary = get("/api/expenses/summary?from=2025-01-01&to=2025-01-31");
        assertThat(summary.statusCode()).isEqualTo(200);
        assertThat(summary.body()).contains("\"count\":2", "\"total\":50.00", "GROCERIES", "PHARMA").doesNotContain("FOOD");
        var filtered = get("/api/expenses?from=2025-01-01&to=2025-01-31&category=GROCERIES");
        assertThat(filtered.body()).contains("Mercado", "35.90").doesNotContain("Farmácia");
    }
    @Test void returnsZeroForEmptyPeriod() throws Exception {
        assertThat(get("/api/expenses/summary?from=2024-01-01&to=2024-01-31").body())
                .contains("\"count\":0", "\"total\":0.00", "\"categories\":[]");
    }
    @Test void rejectsInvalidDataWithoutPersisting() throws Exception {
        for (String amount : new String[]{"0", "-10", "10.001"}) {
            var response = post("{\"description\":\"Inválida\",\"amount\":" + amount +
                ",\"category\":\"FOOD\",\"occurredOn\":\"2024-06-01\"}");
            assertThat(response.statusCode()).isEqualTo(400);
            assertThat(response.headers().firstValue("Content-Type").orElse("")).contains("application/problem+json");
        }
        assertThat(get("/api/expenses/summary?from=2024-06-01&to=2024-06-30").body()).contains("\"count\":0");
    }
    @Test void rejectsMissingCategoryAndFutureDate() throws Exception {
        assertThat(post("""
            {"description":"Sem categoria","amount":1,"occurredOn":"2024-01-01"}
            """).statusCode()).isEqualTo(400);
        assertThat(post("""
            {"description":"Futuro","amount":1,"category":"FOOD","occurredOn":"2999-01-01"}
            """).statusCode()).isEqualTo(400);
    }
    @Test void rejectsInvalidQueryAndJson() throws Exception {
        assertThat(get("/api/expenses?from=2025-02-01&to=2025-01-01").statusCode()).isEqualTo(400);
        assertThat(get("/api/expenses?from=2024-01-01&to=2026-01-01").statusCode()).isEqualTo(400);
        assertThat(get("/api/expenses?from=abc&to=2025-01-01").statusCode()).isEqualTo(400);
        assertThat(post("{\"category\":\"UNKNOWN\"}").statusCode()).isEqualTo(400);
    }
    @Test void healthWorksWithoutApiKey() throws Exception {
        assertThat(get("/actuator/health").statusCode()).isEqualTo(200);
        assertThat(get("/api/assistant/text").statusCode()).isEqualTo(404);
    }
}
