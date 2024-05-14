package com.example.gowork.repository;

import com.example.gowork.model.Workperiod;
import com.example.gowork.model.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkperiodRepository extends JpaRepository<Workperiod, Integer> {
}
