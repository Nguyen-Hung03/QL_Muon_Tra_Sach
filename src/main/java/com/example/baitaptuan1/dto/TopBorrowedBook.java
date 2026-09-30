package com.example.baitaptuan1.dto;

/**
 * Dòng báo cáo Top 5 sách được mượn nhiều nhất (mục 4.6).
 * totalBorrowed là tổng số cuốn đã ghi trên các phiếu mượn.
 */
public class TopBorrowedBook {

    private final Long bookId;
    private final String isbn;
    private final String title;
    private final String author;
    private final Long totalBorrowed;

    public TopBorrowedBook(Long bookId, String isbn, String title, String author, Long totalBorrowed) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalBorrowed = totalBorrowed;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Long getTotalBorrowed() {
        return totalBorrowed;
    }
}
