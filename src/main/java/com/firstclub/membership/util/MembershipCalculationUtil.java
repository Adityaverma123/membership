package com.firstclub.membership.util;

import com.firstclub.membership.domain.MembershipTierRuleEntity;
import com.firstclub.membership.domain.PlanDuration;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

public final class MembershipCalculationUtil {
    private MembershipCalculationUtil() {
    }

    public static Instant calculateEndAt(Instant startAt, PlanDuration duration) {
        return switch (duration) {
            case MONTHLY -> startAt.atOffset(ZoneOffset.UTC).plusMonths(1).toInstant();
            case QUARTERLY -> startAt.atOffset(ZoneOffset.UTC).plusMonths(3).toInstant();
            case YEARLY -> startAt.atOffset(ZoneOffset.UTC).plusYears(1).toInstant();
        };
    }

    public static boolean matchesTierRule(
        MembershipTierRuleEntity rule,
        long lifetimeOrderCount,
        BigDecimal currentMonthOrderValue,
        Set<String> userCohorts
    ) {
        return matchesMinimumOrderCount(rule.getMinOrderCount(), lifetimeOrderCount)
            && matchesMinimumOrderValue(rule.getMinOrderValue(), currentMonthOrderValue)
            && matchesCohort(rule, userCohorts);
    }

    public static Set<String> resolveUserCohorts(String userId) {
        return Math.abs(userId.hashCode()) % 2 == 0 ? Set.of("DEFAULT", "HIGH_INTENT") : Set.of("DEFAULT");
    }

    private static boolean matchesMinimumOrderCount(Integer threshold, long actual) {
        return threshold == null || actual >= threshold;
    }

    private static boolean matchesMinimumOrderValue(BigDecimal threshold, BigDecimal actual) {
        return threshold == null || actual.compareTo(threshold) >= 0;
    }

    private static boolean matchesCohort(MembershipTierRuleEntity rule, Set<String> userCohorts) {
        return rule.getCohorts() == null
            || rule.getCohorts().isEmpty()
            || rule.getCohorts().stream().anyMatch(userCohorts::contains);
    }
}
