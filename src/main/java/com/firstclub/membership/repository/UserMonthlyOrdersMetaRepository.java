package com.firstclub.membership.repository;

import com.firstclub.membership.domain.UserMonthlyOrdersMetaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMonthlyOrdersMetaRepository extends JpaRepository<UserMonthlyOrdersMetaEntity, Long> {
    Optional<UserMonthlyOrdersMetaEntity> findByUserIdAndYearAndMonth(String userId, Integer year, Integer month);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select meta from UserMonthlyOrdersMetaEntity meta
        where meta.userId = :userId
          and meta.year = :year
          and meta.month = :month
        """)
    Optional<UserMonthlyOrdersMetaEntity> findByUserIdAndYearAndMonthForUpdate(
        @Param("userId") String userId,
        @Param("year") Integer year,
        @Param("month") Integer month
    );

    @Query("select coalesce(sum(meta.totalOrders), 0) from UserMonthlyOrdersMetaEntity meta where meta.userId = :userId")
    Long sumTotalOrdersByUserId(@Param("userId") String userId);
}
