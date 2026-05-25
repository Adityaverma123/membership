package com.firstclub.membership.service.mapper;

import com.firstclub.membership.domain.MembershipPlanEntity;
import com.firstclub.membership.domain.MembershipTierEntity;
import com.firstclub.membership.domain.UserMembershipEntity;
import com.firstclub.membership.domain.UserMonthlyOrdersMetaEntity;
import com.firstclub.membership.generated.model.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class MembershipMapper {
    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

    public MembershipPlan toDto(MembershipPlanEntity entity) {
        return new MembershipPlan(
            entity.getId(),
            entity.getName(),
            PlanDuration.valueOf(entity.getDuration().name()),
            RecordStatus.valueOf(entity.getStatus().name()),
            entity.getPrice()
        )
            .description(entity.getDescription())
            .createdAt(toOffsetDateTime(entity.getCreatedAt()))
            .updatedAt(toOffsetDateTime(entity.getUpdatedAt()));
    }

    public MembershipTier toDto(MembershipTierEntity entity) {
        return new MembershipTier(
            entity.getId(),
            entity.getName(),
            TierType.valueOf(entity.getType().name()),
            RecordStatus.valueOf(entity.getStatus().name())
        )
            .description(entity.getDescription())
            .createdAt(toOffsetDateTime(entity.getCreatedAt()))
            .updatedAt(toOffsetDateTime(entity.getUpdatedAt()));
    }

    public UserMembership toDto(UserMembershipEntity entity) {
        return new UserMembership(
            entity.getId(),
            entity.getUserId(),
            toDto(entity.getMembershipPlan()),
            toDto(entity.getMembershipTier()),
            toOffsetDateTime(entity.getStartAt()),
            toOffsetDateTime(entity.getEndAt())
        )
            .cancelledAt(toOffsetDateTime(entity.getCancelledAt()))
            .createdAt(toOffsetDateTime(entity.getCreatedAt()))
            .updatedAt(toOffsetDateTime(entity.getUpdatedAt()));
    }

    public UserMonthlyOrdersMeta toDto(UserMonthlyOrdersMetaEntity entity) {
        return new UserMonthlyOrdersMeta(
            entity.getId(),
            entity.getUserId(),
            entity.getYear(),
            entity.getMonth(),
            entity.getTotalOrders(),
            entity.getTotalOrderValue()
        )
            .createdAt(toOffsetDateTime(entity.getCreatedAt()))
            .updatedAt(toOffsetDateTime(entity.getUpdatedAt()));
    }
}
