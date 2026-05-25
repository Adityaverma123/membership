package com.firstclub.membership.service;

import com.firstclub.membership.domain.UserMonthlyOrdersMetaEntity;
import com.firstclub.membership.repository.UserMonthlyOrdersMetaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import com.firstclub.membership.service.mapper.MembershipMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.firstclub.membership.generated.model.UserMonthlyOrdersMeta;

@Service
public class OrderAggregateService {
    private final UserMonthlyOrdersMetaRepository userMonthlyOrdersMetaRepository;
    private final MembershipMapper mapper;
    private final UserMembershipSubscriptionService userMembershipSubscriptionService;

    public OrderAggregateService(
        UserMonthlyOrdersMetaRepository userMonthlyOrdersMetaRepository,
        MembershipMapper mapper,
        UserMembershipSubscriptionService userMembershipSubscriptionService
    ) {
        this.userMonthlyOrdersMetaRepository = userMonthlyOrdersMetaRepository;
        this.mapper = mapper;
        this.userMembershipSubscriptionService = userMembershipSubscriptionService;
    }

    @Transactional
    public UserMonthlyOrdersMeta ingestOrder(String userId, BigDecimal orderValue) {
        Instant now = Instant.now();
        int year = now.atOffset(ZoneOffset.UTC).getYear();
        int month = now.atOffset(ZoneOffset.UTC).getMonthValue();

        UserMonthlyOrdersMetaEntity aggregate = userMonthlyOrdersMetaRepository
            .findByUserIdAndYearAndMonthForUpdate(userId, year, month)
            .orElseGet(() -> newAggregate(userId, now));

        aggregate.setTotalOrders(aggregate.getTotalOrders() + 1);
        aggregate.setTotalOrderValue(aggregate.getTotalOrderValue().add(orderValue));

        UserMonthlyOrdersMetaEntity saved = userMonthlyOrdersMetaRepository.save(aggregate);

        userMembershipSubscriptionService.updateMembershipTierIfExists(userId);

        return mapper.toDto(saved);
    }

    public long getLifetimeOrderCount(String userId) {
        return userMonthlyOrdersMetaRepository.sumTotalOrdersByUserId(userId);
    }

    public BigDecimal getCurrentMonthOrderValue(String userId) {
        Instant now = Instant.now();
        return userMonthlyOrdersMetaRepository.findByUserIdAndYearAndMonth(userId, now.atOffset(ZoneOffset.UTC).getYear(), now.atOffset(ZoneOffset.UTC).getMonthValue())
            .map(UserMonthlyOrdersMetaEntity::getTotalOrderValue)
            .orElse(BigDecimal.ZERO);
    }

    public UserMonthlyOrdersMetaEntity newAggregate(String userId, Instant now) {
        UserMonthlyOrdersMetaEntity aggregate = new UserMonthlyOrdersMetaEntity();
        aggregate.setUserId(userId);
        aggregate.setYear(now.atOffset(ZoneOffset.UTC).getYear());
        aggregate.setMonth(now.atOffset(ZoneOffset.UTC).getMonthValue());
        aggregate.setTotalOrders(0);
        aggregate.setTotalOrderValue(BigDecimal.ZERO);
        return aggregate;
    }

    public Optional<UserMonthlyOrdersMetaEntity> findByUserIdAndYearAndMonthForUpdate(String userId, Integer year, Integer month) {
        return userMonthlyOrdersMetaRepository.findByUserIdAndYearAndMonthForUpdate(userId, year, month);
    }
}
