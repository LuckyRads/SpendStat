package com.spendstat.adapter.out.persistence.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    @Query("SELECT c FROM CategoryJpaEntity c WHERE c.isDefault = true OR c.userId = :userId ORDER BY c.isDefault DESC, c.name ASC")
    List<CategoryJpaEntity> findAllVisibleToUser(@Param("userId") UUID userId);
}
