package com.example.courier.repository;

import com.example.courier.domain.BanHistory;
import com.example.courier.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanHistoryRepository extends JpaRepository<BanHistory, Long> {
    List<BanHistory> findByPersonOrderByActionTimeDesc(Person person);
}
