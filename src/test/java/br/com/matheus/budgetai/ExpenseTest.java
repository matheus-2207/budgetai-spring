package br.com.matheus.budgetai;

import br.com.matheus.budgetai.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class ExpenseTest {
    private Expense expense(String amount) {
        return new Expense(UUID.randomUUID(), "  Mercado  ", new BigDecimal(amount), Category.GROCERIES,
                LocalDate.of(2026, 1, 10), Instant.parse("2026-01-10T12:00:00Z"));
    }
    @Test void preservesMoneyExactlyAndNormalizesDescription() {
        assertThat(expense("35.90").amount()).isEqualByComparingTo("35.90");
        assertThat(expense("35.90").description()).isEqualTo("Mercado");
    }
    @ParameterizedTest @ValueSource(strings = {"0", "-1", "0.001", "1000000000"})
    void rejectsInvalidAmounts(String amount) {
        assertThatThrownBy(() -> expense(amount)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void rejectsReversedAndOverlongPeriods() {
        var day = LocalDate.of(2026, 1, 10);
        assertThatThrownBy(() -> new Period(day, day.minusDays(1))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Period(day, day.plusDays(366))).isInstanceOf(IllegalArgumentException.class);
    }
}
