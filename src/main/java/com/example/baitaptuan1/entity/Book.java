package com.example.baitaptuan1.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;

    private BigDecimal totalQuantity;

    private BigDecimal availableQuantity;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowingDetail> borrowingDetails;

    public Book(){ }
    public Book(Long id, String isbn, String title, BigDecimal totalQuantity, String author, BigDecimal availableQuantity, Boolean status, Category category, List<BorrowingDetail> borrowingDetails) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.totalQuantity = totalQuantity;
        this.author = author;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.category = category;
        this.borrowingDetails = borrowingDetails;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<BorrowingDetail> getBorrowingDetails() {
        return borrowingDetails;
    }

    public void setBorrowingDetails(List<BorrowingDetail> borrowingDetails) {
        this.borrowingDetails = borrowingDetails;
    }
}
