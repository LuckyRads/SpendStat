package com.spendstat.adapter.out.persistence.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TransactionJpaRepository extends JpaRepository<TransactionJpaEntity, UUID>,
        JpaSpecificationExecutor<TransactionJpaEntity> {
}
