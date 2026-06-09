package com.premiumhub.backend.repository;

import com.premiumhub.backend.entity.Policy;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Policy p WHERE p.policyId = :id")
    Optional<Policy> findByIdWithLock(@Param("id") Long id);
}
