package br.com.matheus.budgetai.domain;
import java.util.List;
public interface ExpenseRepository {
    Expense save(Expense expense);
    List<Expense> find(Period period, Category category);
    List<CategoryTotal> summarize(Period period);
}
