package com.firstclub.membership.service;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.exception.ApiException;
import com.firstclub.membership.generated.model.MembershipPlan;
import com.firstclub.membership.generated.model.MembershipTier;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.MembershipTierRuleRepository;
import com.firstclub.membership.repository.UserMonthlyOrdersMetaRepository;
import com.firstclub.membership.service.mapper.MembershipMapper;
import com.firstclub.membership.util.MembershipCalculationUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
    private final MembershipPlanRepository membershipPlanRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final MembershipTierRuleRepository membershipTierRuleRepository;
    private final UserMonthlyOrdersMetaRepository userMonthlyOrdersMetaRepository;
    private final MembershipMapper mapper;

    public MembershipService(
        MembershipPlanRepository membershipPlanRepository,
        MembershipTierRepository membershipTierRepository,
        MembershipTierRuleRepository membershipTierRuleRepository,
        UserMonthlyOrdersMetaRepository userMonthlyOrdersMetaRepository,
        MembershipMapper mapper
    ) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.membershipTierRepository = membershipTierRepository;
        this.membershipTierRuleRepository = membershipTierRuleRepository;
        this.userMonthlyOrdersMetaRepository = userMonthlyOrdersMetaRepository;
        this.mapper = mapper;
    }

    public List<MembershipPlan> getPlans() {
        return membershipPlanRepository.findAllByStatusOrderByPriceAsc(RecordStatus.ACTIVE)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    public List<MembershipTier> getTiers() {
        return membershipTierRepository.findAllByStatusOrderByIdAsc(RecordStatus.ACTIVE)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    public MembershipTierEntity determineTier(String userId) {

        long lifetimeOrderCount = userMonthlyOrdersMetaRepository.sumTotalOrdersByUserId(userId);

        BigDecimal currentMonthOrderValue = userMonthlyOrdersMetaRepository
            .findByUserIdAndYearAndMonth(userId, Instant.now().atOffset(ZoneOffset.UTC).getYear(), Instant.now().atOffset(ZoneOffset.UTC).getMonthValue())
            .map(UserMonthlyOrdersMetaEntity::getTotalOrderValue)
            .orElse(BigDecimal.ZERO);

        Set<String> userCohorts = MembershipCalculationUtil.resolveUserCohorts(userId);

        return membershipTierRuleRepository.findAllByStatus(RecordStatus.ACTIVE)
                .stream()
                .filter(rule -> rule.getMembershipTier().getStatus() == RecordStatus.ACTIVE)
                .filter(rule -> MembershipCalculationUtil.matchesTierRule(
                        rule,
                        lifetimeOrderCount,
                        currentMonthOrderValue,
                        userCohorts
                ))
                .map(MembershipTierRuleEntity::getMembershipTier)
                .max(Comparator.comparingInt(tier -> tier.getType().getRank()))
                .orElseGet(() ->
                        membershipTierRepository
                                .findByTypeAndStatus(TierType.SILVER, RecordStatus.ACTIVE)
                                .orElseThrow(() -> new ApiException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Default SILVER tier is not configured"
                                ))
                );
    }

    protected MembershipPlanEntity getPlanById(Long membershipPlanId) {
        return membershipPlanRepository.findById(membershipPlanId)
            .filter(candidate -> candidate.getStatus() == RecordStatus.ACTIVE)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Active membership plan not found"));
    }
}
