package br.com.matheus.budgetai.application;
import br.com.matheus.budgetai.domain.Category;
import java.math.BigDecimal;
import java.time.LocalDate;
public record CreateExpense(String description, BigDecimal amount, Category category, LocalDate occurredOn) { }
