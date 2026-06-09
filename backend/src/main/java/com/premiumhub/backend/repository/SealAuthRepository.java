package com.premiumhub.backend.repository;

import com.premiumhub.backend.entity.SealAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SealAuthRepository extends JpaRepository<SealAuth, Long> {

    @Query("SELECT s FROM SealAuth s JOIN FETCH s.payment")
    List<SealAuth> findAllWithPayment();
}
