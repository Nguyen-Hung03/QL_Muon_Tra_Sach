package com.example.baitaptuan1.dto;

public class BorrowLine {

    private final Long bookId;
    private final Integer quantity;

    public BorrowLine(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
