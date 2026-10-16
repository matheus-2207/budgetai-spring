package br.com.matheus.budgetai.infrastructure.persistence;

import br.com.matheus.budgetai.application.CreateExpense;
import br.com.matheus.budgetai.application.ExpenseService;
import br.com.matheus.budgetai.domain.Category;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Component
@Profile("demo")
public class DemoData implements ApplicationRunner {
    private final ExpenseService expenses;
    private final Clock clock;
    public DemoData(ExpenseService expenses, Clock clock) {
        this.expenses = expenses;
        this.clock = clock;
    }
    @Override
    public void run(ApplicationArguments args) {
        var today = LocalDate.now(clock);
        expenses.create(new CreateExpense("DEMONSTRAÇÃO — Compras da semana", new BigDecimal("182.90"), Category.GROCERIES, today));
        expenses.create(new CreateExpense("DEMONSTRAÇÃO — Almoço", new BigDecimal("34.50"), Category.FOOD, today));
        expenses.create(new CreateExpense("DEMONSTRAÇÃO — Farmácia", new BigDecimal("48.00"), Category.PHARMA, today));
        expenses.create(new CreateExpense("DEMONSTRAÇÃO — Transporte", new BigDecimal("22.80"), Category.AUTO, today));
        expenses.create(new CreateExpense("DEMONSTRAÇÃO — Cinema", new BigDecimal("30.00"), Category.LEISURE, today));
    }
}
