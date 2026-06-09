package com.premiumhub.backend.repository;

import com.premiumhub.backend.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
