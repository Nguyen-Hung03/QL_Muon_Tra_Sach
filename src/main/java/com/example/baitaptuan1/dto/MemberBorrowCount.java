package com.example.baitaptuan1.dto;

/**
 * Dòng báo cáo số lượt mượn theo thành viên (mục 4.6).
 * borrowCount là số phiếu mượn của thành viên đó.
 */
public class MemberBorrowCount {

    private final Long memberId;
    private final String fullName;
    private final String email;
    private final Long borrowCount;

    public MemberBorrowCount(Long memberId, String fullName, String email, Long borrowCount) {
        this.memberId = memberId;
        this.fullName = fullName;
        this.email = email;
        this.borrowCount = borrowCount;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Long getBorrowCount() {
        return borrowCount;
    }
}
