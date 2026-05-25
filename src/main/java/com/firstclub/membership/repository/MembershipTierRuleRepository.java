package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipTierRuleEntity;
import com.firstclub.membership.domain.RecordStatus;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTierRuleRepository extends JpaRepository<MembershipTierRuleEntity, Long> {
    @EntityGraph(attributePaths = "membershipTier")
    List<MembershipTierRuleEntity> findAllByStatus(RecordStatus status);
}
