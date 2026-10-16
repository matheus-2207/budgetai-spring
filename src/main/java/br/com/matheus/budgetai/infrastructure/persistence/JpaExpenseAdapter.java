package br.com.matheus.budgetai.infrastructure.persistence;

import br.com.matheus.budgetai.domain.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class JpaExpenseAdapter implements ExpenseRepository {
    private final ExpenseJpaRepository repository;
    public JpaExpenseAdapter(ExpenseJpaRepository repository) { this.repository = repository; }
    @Override public Expense save(Expense expense) { return repository.save(new ExpenseEntity(expense)).toDomain(); }
    @Override public List<Expense> find(Period period, Category category) {
        return repository.search(period.from(), period.to(), category, PageRequest.of(0, 200))
                .stream().map(ExpenseEntity::toDomain).toList();
    }
    @Override public List<CategoryTotal> summarize(Period period) { return repository.summarize(period.from(), period.to()); }
}
