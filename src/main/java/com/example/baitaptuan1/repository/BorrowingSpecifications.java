package com.example.baitaptuan1.repository;

import com.example.baitaptuan1.entity.Borrowing;
import com.example.baitaptuan1.entity.BorrowingStatus;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class BorrowingSpecifications {

    private BorrowingSpecifications() {
    }

    public static Specification<Borrowing> filter(BorrowingStatus status, Long memberId,
                                                  LocalDateTime fromDate, LocalDateTime toDate,
                                                  boolean overdueOnly) {
        return (root, query, cb) -> {
            if (query.getResultType() == Borrowing.class) {
                root.fetch("member", JoinType.LEFT);
                query.distinct(true);
            }
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (memberId != null) {
                predicates.add(cb.equal(root.get("member").get("id"), memberId));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("borrowDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThan(root.get("borrowDate"), toDate));
            }
            if (overdueOnly) {
                predicates.add(cb.lessThan(root.get("dueDate"), LocalDateTime.now()));
                predicates.add(cb.notEqual(root.get("status"), BorrowingStatus.RETURNED));
            }
            query.orderBy(cb.desc(root.get("borrowDate")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
