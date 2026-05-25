package com.firstclub.membership.repository;

import com.firstclub.membership.domain.MembershipTierEntity;
import com.firstclub.membership.domain.RecordStatus;
import com.firstclub.membership.domain.TierType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTierRepository extends JpaRepository<MembershipTierEntity, Long> {
    List<MembershipTierEntity> findAllByStatusOrderByIdAsc(RecordStatus status);

    Optional<MembershipTierEntity> findByTypeAndStatus(TierType type, RecordStatus status);
}
