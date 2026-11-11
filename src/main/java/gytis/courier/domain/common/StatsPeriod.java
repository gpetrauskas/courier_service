package gytis.courier.domain.common;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public enum StatsPeriod {
    DAY,
    WEEK,
    MONTH;

    public LocalDateTime startFrom(LocalDateTime dateTime) {
        return switch (this) {
            case DAY -> dateTime.toLocalDate().atStartOfDay();
            case WEEK -> dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay();
            case MONTH -> dateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        };
    }
}
