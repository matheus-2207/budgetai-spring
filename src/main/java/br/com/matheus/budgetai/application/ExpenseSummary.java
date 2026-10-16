package br.com.matheus.budgetai.application;
import br.com.matheus.budgetai.domain.CategoryTotal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record ExpenseSummary(LocalDate from, LocalDate to, long count, BigDecimal total,
                             List<CategoryTotal> categories) { }
