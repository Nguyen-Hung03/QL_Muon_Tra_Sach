package com.example.baitaptuan1.entity;

public enum BorrowingStatus {
    BORROWING("Đang mượn"),
    RETURNED("Đã trả"),
    OVERDUE("Quá hạn");

    private final String label;

    BorrowingStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isClosed() {
        return this == RETURNED;
    }
}
