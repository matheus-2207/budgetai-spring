package br.com.matheus.budgetai.infrastructure.ai;

import br.com.matheus.budgetai.application.*;
import br.com.matheus.budgetai.domain.Category;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.function.Supplier;

@Component
public class BudgetTools {
    private final ExpenseService service;
    public BudgetTools(ExpenseService service) { this.service = service; }
    public record ToolResult(boolean success, Object data, String error) { }
    @Tool(description = "Registra uma despesa explicitamente solicitada. Valor em reais, até duas casas decimais.")
    public ToolResult createExpense(@ToolParam(description = "Descrição do gasto") String description,
            @ToolParam(description = "Valor em REAIS; por exemplo, 35.90") BigDecimal amount,
            Category category, @ToolParam(description = "Data ISO AAAA-MM-DD") String occurredOn) {
        return safely(() -> service.create(new CreateExpense(description, amount, category, date(occurredOn))));
    }
    @Tool(description = "Lista até 200 despesas mais recentes em um período inclusivo, com categoria opcional.")
    public ToolResult listExpenses(String from, String to, @ToolParam(required = false) Category category) {
        return safely(() -> service.list(date(from), date(to), category));
    }
    @Tool(description = "Calcula o total, quantidade e totais por categoria de TODAS as despesas do período inclusivo.")
    public ToolResult summarizeExpenses(@ToolParam(description = "Data inicial ISO AAAA-MM-DD") String from,
                                        @ToolParam(description = "Data final ISO AAAA-MM-DD") String to) {
        return safely(() -> service.summarize(date(from), date(to)));
    }
    private LocalDate date(String value) {
        try { return LocalDate.parse(value); }
        catch (RuntimeException ex) { throw new IllegalArgumentException("Informe datas no formato AAAA-MM-DD."); }
    }
    private ToolResult safely(Supplier<?> action) {
        try { return new ToolResult(true, action.get(), null); }
        catch (IllegalArgumentException ex) { return new ToolResult(false, null, ex.getMessage()); }
    }
}
