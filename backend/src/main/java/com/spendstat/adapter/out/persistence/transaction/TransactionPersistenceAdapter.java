package com.spendstat.adapter.out.persistence.transaction;

import com.spendstat.domain.transaction.Transaction;
import com.spendstat.domain.transaction.TransactionFilter;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionPage;
import com.spendstat.domain.transaction.port.out.TransactionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return jpaRepository.save(TransactionJpaEntity.fromDomain(transaction)).toDomain();
    }

    @Override
    public Optional<Transaction> findById(TransactionId id) {
        return jpaRepository.findById(id.getValue()).map(TransactionJpaEntity::toDomain);
    }

    @Override
    public TransactionPage findByFilter(TransactionFilter filter) {
        PageRequest pageRequest = PageRequest.of(
                filter.page(), filter.size(),
                Sort.by(Sort.Direction.DESC, "txDate", "createdAt")
        );
        Page<TransactionJpaEntity> page = jpaRepository.findAll(specificationFor(filter), pageRequest);
        List<Transaction> transactions = page.getContent().stream()
                .map(TransactionJpaEntity::toDomain)
                .toList();
        return new TransactionPage(transactions, page.getTotalElements());
    }

    @Override
    public void deleteById(TransactionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    private Specification<TransactionJpaEntity> specificationFor(TransactionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), filter.userId().getValue()));
            if (filter.accountId() != null) {
                predicates.add(cb.equal(root.get("accountId"), filter.accountId()));
            }
            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), filter.categoryId()));
            }
            if (filter.type() != null) {
                predicates.add(cb.equal(root.get("type"), filter.type().name()));
            }
            if (filter.from() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("txDate"), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("txDate"), filter.to()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
