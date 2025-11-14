package com.rakesh.finflow.common.spec;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.Instant;

public class SpecBuilder<T> {

    private Specification<T> spec;

    private SpecBuilder() {
        this.spec = (root, query, cb) -> cb.conjunction();
    }

    public static <T> SpecBuilder<T> of(Class<T> clazz) {
        return new SpecBuilder<>();
    }

    // -------------------
    // Equality
    // -------------------
    public SpecBuilder<T> eq(String field, Object value) {
        if (value != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get(field), value));
        }
        return this;
    }

    public SpecBuilder<T> ne(String field, Object value) {
        if (value != null) {
            spec = spec.and((root, query, cb) -> cb.notEqual(root.get(field), value));
        }
        return this;
    }

    // -------------------
    // LIKE
    // -------------------
    public SpecBuilder<T> like(String field, String value) {
        if (value != null && !value.isBlank()) {
            String pattern = "%" + value.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(field).as(String.class)), pattern)
            );
        }
        return this;
    }

    // -------------------
    // BETWEEN - Instant
    // -------------------
    public SpecBuilder<T> betweenInstant(String field, Instant start, Instant end) {
        if (start != null && end != null) {
            spec = spec.and((root, query, cb) -> {
                Expression<Instant> exp = root.get(field).as(Instant.class);
                return cb.between(exp, start, end);
            });
        }
        return this;
    }

    // -------------------
    // BETWEEN - BigDecimal
    // -------------------
    public SpecBuilder<T> betweenBigDecimal(String field, BigDecimal min, BigDecimal max) {
        if (min != null && max != null) {
            spec = spec.and((root, query, cb) -> {
                Expression<BigDecimal> exp = root.get(field).as(BigDecimal.class);
                return cb.between(exp, min, max);
            });
        }
        return this;
    }

    // -------------------
    // BETWEEN - Integer
    // -------------------
    public SpecBuilder<T> betweenInt(String field, Integer min, Integer max) {
        if (min != null && max != null) {
            spec = spec.and((root, query, cb) -> {
                Expression<Integer> exp = root.get(field).as(Integer.class);
                return cb.between(exp, min, max);
            });
        }
        return this;
    }

    // -------------------
    // IN
    // -------------------
    public SpecBuilder<T> in(String field, Iterable<?> values) {
        if (values != null && values.iterator().hasNext()) {
            spec = spec.and((root, query, cb) -> root.get(field).in(values));
        }
        return this;
    }

    // -------------------
    // FINAL BUILD
    // -------------------
    public Specification<T> build() {
        return spec;
    }
}
