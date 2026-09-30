package com.example.baitaptuan1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ và tên không được để trống")
    @Column(nullable = false, length = 150)
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    @Column(nullable = false, unique = true, length = 100)
    private String email; // Đảm bảo email duy nhất và không bị trùng lặp

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private Boolean status = true; // true: Hoạt động, false: Bị khóa (Dùng để lọc trạng thái ở mục 6)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Borrowing> borrowings;

    // Tự động gán thời gian hiện tại khi thêm mới thành viên vào hệ thống
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- CONSTRUCTOR ---
    public Member() {
    }

    public Member(Long id, String fullName, String email, String phone, Boolean status, LocalDateTime createdAt, List<Borrowing> borrowings) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.borrowings = borrowings;
    }

    // --- GETTER VÀ SETTER ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Họ và tên không được để trống") String getFullName() {
        return fullName;
    }

    public void setFullName(@NotBlank(message = "Họ và tên không được để trống") String fullName) {
        this.fullName = fullName;
    }

    public @Email(message = "Email không đúng định dạng") @NotBlank(message = "Email không được để trống") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email không đúng định dạng") @NotBlank(message = "Email không được để trống") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Số điện thoại không được để trống") String getPhone() {
        return phone;
    }

    public void setPhone(@NotBlank(message = "Số điện thoại không được để trống") String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    public void setBorrowings(List<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }
}
