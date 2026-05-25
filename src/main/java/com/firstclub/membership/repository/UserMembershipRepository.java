package com.firstclub.membership.repository;

import com.firstclub.membership.domain.UserMembershipEntity;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMembershipRepository extends JpaRepository<UserMembershipEntity, Long> {
    @EntityGraph(attributePaths = {"membershipPlan", "membershipTier"})
    Optional<UserMembershipEntity> findFirstByUserIdAndCancelledAtIsNullAndEndAtAfterOrderByCreatedAtDesc(
        String userId,
        Instant now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"membershipPlan", "membershipTier"})
    @Query("""
        select um from UserMembershipEntity um
        where um.userId = :userId
          and um.cancelledAt is null
          and um.endAt > :now
        order by um.createdAt desc
        """)
    List<UserMembershipEntity> findCurrentForUpdate(@Param("userId") String userId, @Param("now") Instant now);
}
