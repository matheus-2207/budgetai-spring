package br.com.matheus.budgetai;

import br.com.matheus.budgetai.application.*;
import br.com.matheus.budgetai.domain.*;
import br.com.matheus.budgetai.infrastructure.ai.BudgetTools;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetToolsTest {
    private final ExpenseRepository repository = mock(ExpenseRepository.class);
    private final ExpenseService service = new ExpenseService(repository,
            Clock.fixed(Instant.parse("2026-01-15T12:00:00Z"), ZoneOffset.UTC));
    private final BudgetTools tools = new BudgetTools(service);
    @Test void toolsRejectInvalidCreationBeforeSaving() {
        assertThat(tools.createExpense("Mercado", new BigDecimal("-1"), Category.GROCERIES, "2026-01-01").success()).isFalse();
        assertThat(tools.createExpense("Mercado", BigDecimal.ONE, Category.GROCERIES, "2026-01-16").success()).isFalse();
        assertThat(tools.createExpense("Mercado", BigDecimal.ONE, Category.GROCERIES, "ontem").success()).isFalse();
        verifyNoInteractions(repository);
    }
    @Test void summaryUsesStoredAmountsInsteadOfModelArithmetic() {
        when(repository.summarize(new Period(LocalDate.of(2026,1,1), LocalDate.of(2026,1,31))))
            .thenReturn(List.of(new CategoryTotal(Category.FOOD, 2, new BigDecimal("0.30"))));
        var response = tools.summarizeExpenses("2026-01-01", "2026-01-31");
        assertThat(response.success()).isTrue();
        assertThat(((ExpenseSummary) response.data()).total()).isEqualByComparingTo("0.30");
    }
    @Test void springAiCanGenerateSchemasForAllThreeTools() {
        var callbacks = MethodToolCallbackProvider.builder().toolObjects(tools).build().getToolCallbacks();
        assertThat(callbacks).hasSize(3);
        assertThat(callbacks).extracting(c -> c.getToolDefinition().name())
            .containsExactlyInAnyOrder("createExpense", "listExpenses", "summarizeExpenses");
        assertThat(callbacks).allSatisfy(c -> assertThat(c.getToolDefinition().inputSchema()).contains("properties"));
    }
}
