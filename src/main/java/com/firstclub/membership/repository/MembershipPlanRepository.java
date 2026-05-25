package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipPlanEntity;
import com.firstclub.membership.domain.RecordStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlanEntity, Long> {
    List<MembershipPlanEntity> findAllByStatusOrderByPriceAsc(RecordStatus status);
}
