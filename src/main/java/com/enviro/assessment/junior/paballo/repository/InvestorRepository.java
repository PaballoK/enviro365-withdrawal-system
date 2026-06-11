package com.enviro.assessment.junior.paballo.repository;

import com.enviro.assessment.junior.paballo.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Long> {

    Optional<Investor> findByEmail(String email);

    @Query("SELECT i FROM Investor i JOIN FETCH i.products WHERE i.id = :id")
    Optional<Investor> findByIdWithProducts(@Param("id") Long id);

}
