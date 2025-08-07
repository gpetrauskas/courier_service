package com.example.courier.repository;

import com.example.courier.common.OrderStatus;
import com.example.courier.domain.Person;
import com.example.courier.domain.User;
import com.example.courier.dto.UserWithOrdersCountByStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    Optional<User> findById(Long id);

    @Query("""
            SELECT new com.example.courier.dto.UserWithOrdersCountByStatus(u, COUNT(o))
            FROM User u
            LEFT JOIN u.orders o
            WHERE u.id = :userId AND o.status = :orderStatus
            GROUP BY u
            """)
    Optional<UserWithOrdersCountByStatus> findUserWithOrdersCountByStatus(
            @Param("userId") Long userId, @Param("orderStatus") OrderStatus orderStatus);
}
