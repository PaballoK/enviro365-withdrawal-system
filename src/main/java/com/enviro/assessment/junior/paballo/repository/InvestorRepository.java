package com.enviro.assessment.junior.paballo.repository;

import com.enviro.assessment.junior.paballo.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {
}
