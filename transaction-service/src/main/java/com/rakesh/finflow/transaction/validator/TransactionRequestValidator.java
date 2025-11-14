package com.rakesh.finflow.transaction.validator;

import com.rakesh.finflow.transaction.dto.TransactionSearchRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class TransactionRequestValidator {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "transactionAt",
            "category",
            "type",
            "sourceId",
            "sourceName",
            "description",
            "amount"
    );
    private static final Set<String> ALLOWED_SORT_ORDERS = Set.of("asc", "desc");


    public void validateSortBy(String sortBy, TransactionSearchRequest req) {

        if (sortBy == null || sortBy.isBlank()) {
            return; // no sorting provided → default sorting applies
        }

        // 1. Must be an allowed field
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy field: " + sortBy);
        }

        // 2. If sorting is requested on a specific field, that field must not be null in request
        switch (sortBy) {
            case "category" -> {
                if (req.getCategory() == null || req.getCategory().isBlank()) {
                    throw new IllegalArgumentException("Cannot sort by category when category filter is null.");
                }
            }
            case "type" -> {
                if (req.getType() == null) {
                    throw new IllegalArgumentException("Cannot sort by type when type filter is null.");
                }
            }
            case "sourceId" -> {
                if (req.getSourceId() == null || req.getSourceId().isBlank()) {
                    throw new IllegalArgumentException("Cannot sort by sourceId when request sourceId is null.");
                }
            }
            case "sourceName" -> {
                if (req.getSourceName() == null || req.getSourceName().isBlank()) {
                    throw new IllegalArgumentException("Cannot sort by sourceName when request sourceName is null.");
                }
            }
            case "description" -> {
                if (req.getKeyword() == null || req.getKeyword().isBlank()) {
                    throw new IllegalArgumentException("Cannot sort by description when keyword is null.");
                }
            }
            case "transactionAt" -> {
                if (req.getStartDate() == null || req.getEndDate() == null) {
                    throw new IllegalArgumentException("Cannot sort by transactionAt without date range.");
                }
            }
            default -> {
                // nothing extra
            }
        }
    }


    public void validateSortOrder(String sortOrder) {
        if (sortOrder == null || sortOrder.isBlank()) {
            return; // default is fine
        }
        if (!ALLOWED_SORT_ORDERS.contains(sortOrder.toLowerCase())) {
            throw new IllegalArgumentException("Invalid sorting order: " + sortOrder);
        }
    }

    public void validateSortParams(String sortBy, String sortingOrder, TransactionSearchRequest req) {
        validateSortBy(sortBy, req);
        validateSortOrder(sortingOrder);
    }


}
