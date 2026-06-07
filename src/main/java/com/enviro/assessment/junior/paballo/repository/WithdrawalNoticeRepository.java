package com.enviro.assessment.junior.paballo.repository;

import com.enviro.assessment.junior.paballo.entity.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    List<WithdrawalNotice> findByInvestorIdOrderByProcessedAtDesc(Long investorId);
}
