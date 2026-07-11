package com.wilddex.repository;

import com.wilddex.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByUserId(Long userId);

    long countByUserId(Long userId);
}
