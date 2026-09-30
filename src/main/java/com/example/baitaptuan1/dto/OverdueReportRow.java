package com.example.baitaptuan1.dto;

import com.example.baitaptuan1.entity.BorrowingStatus;

import java.time.LocalDateTime;

public class OverdueReportRow {

    private final Long borrowingId;
    private final String memberName;
    private final LocalDateTime borrowDate;
    private final LocalDateTime dueDate;
    private final long lateDays;
    private final BorrowingStatus status;

    public OverdueReportRow(Long borrowingId, String memberName, LocalDateTime borrowDate, LocalDateTime dueDate,
                            long lateDays, BorrowingStatus status) {
        this.borrowingId = borrowingId;
        this.memberName = memberName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.lateDays = lateDays;
        this.status = status;
    }

    public Long getBorrowingId() {
        return borrowingId;
    }

    public String getMemberName() {
        return memberName;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public long getLateDays() {
        return lateDays;
    }

    public BorrowingStatus getStatus() {
        return status;
    }
}
