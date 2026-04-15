package com.spendstat.adapter.in.web;

import com.spendstat.adapter.in.web.dto.transaction.CreateTransactionRequest;
import com.spendstat.adapter.in.web.dto.transaction.TransactionResponse;
import com.spendstat.adapter.in.web.dto.transaction.UpdateTransactionRequest;
import com.spendstat.domain.shared.UserId;
import com.spendstat.domain.transaction.TransactionFilter;
import com.spendstat.domain.transaction.TransactionId;
import com.spendstat.domain.transaction.TransactionPage;
import com.spendstat.domain.transaction.TransactionType;
import com.spendstat.domain.transaction.port.in.CreateTransactionUseCase;
import com.spendstat.domain.transaction.port.in.DeleteTransactionUseCase;
import com.spendstat.domain.transaction.port.in.QueryTransactionsUseCase;
import com.spendstat.domain.transaction.port.in.UpdateTransactionUseCase;
import com.spendstat.infrastructure.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final QueryTransactionsUseCase queryTransactionsUseCase;

    @GetMapping
    public PagedResponse list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        UserId userId = SecurityUtils.getCurrentUserId();
        TransactionFilter filter = new TransactionFilter(userId, accountId, categoryId, type, from, to, page, size);
        TransactionPage result = queryTransactionsUseCase.queryTransactions(filter);
        List<TransactionResponse> items = result.transactions().stream()
                .map(TransactionResponse::from).toList();
        return new PagedResponse(items, result.totalCount());
    }

    @GetMapping("/{id}")
    public TransactionResponse getById(@PathVariable UUID id) {
        return TransactionResponse.from(
                queryTransactionsUseCase.getTransactionById(
                        TransactionId.of(id), SecurityUtils.getCurrentUserId())
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return TransactionResponse.from(createTransactionUseCase.createTransaction(
                new CreateTransactionUseCase.CreateTransactionCommand(
                        SecurityUtils.getCurrentUserId(),
                        request.accountId(),
                        request.categoryId(),
                        request.amount(),
                        request.currency(),
                        request.type(),
                        request.description(),
                        request.merchant(),
                        request.txDate()
                )
        ));
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@PathVariable UUID id,
                                      @Valid @RequestBody UpdateTransactionRequest request) {
        return TransactionResponse.from(updateTransactionUseCase.updateTransaction(
                new UpdateTransactionUseCase.UpdateTransactionCommand(
                        TransactionId.of(id),
                        SecurityUtils.getCurrentUserId(),
                        request.categoryId(),
                        request.description(),
                        request.merchant()
                )
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteTransactionUseCase.deleteTransaction(
                TransactionId.of(id), SecurityUtils.getCurrentUserId()
        );
    }

    public record PagedResponse(List<TransactionResponse> transactions, long totalCount) {
    }
}
