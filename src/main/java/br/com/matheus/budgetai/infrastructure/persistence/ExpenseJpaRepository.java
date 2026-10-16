package br.com.matheus.budgetai.infrastructure.persistence;

import br.com.matheus.budgetai.domain.Category;
import br.com.matheus.budgetai.domain.CategoryTotal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, UUID> {
    @Query("select e from ExpenseEntity e where e.occurredOn between :from and :to " +
           "and (:category is null or e.category = :category) order by e.occurredOn desc, e.createdAt desc, e.id")
    List<ExpenseEntity> search(@Param("from") LocalDate from, @Param("to") LocalDate to,
                              @Param("category") Category category, Pageable pageable);
    @Query("select new br.com.matheus.budgetai.domain.CategoryTotal(e.category, count(e), sum(e.amount)) " +
           "from ExpenseEntity e where e.occurredOn between :from and :to group by e.category order by e.category")
    List<CategoryTotal> summarize(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
