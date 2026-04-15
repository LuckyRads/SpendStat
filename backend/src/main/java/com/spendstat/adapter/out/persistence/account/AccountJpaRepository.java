package com.spendstat.adapter.out.persistence.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {

    List<AccountJpaEntity> findByUserId(UUID userId);
}
