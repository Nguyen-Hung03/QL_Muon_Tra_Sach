package com.example.baitaptuan1.repository;

import com.example.baitaptuan1.dto.MemberBorrowCount;
import com.example.baitaptuan1.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    // 1. Khai báo hàm phục vụ lọc danh sách thành viên theo trạng thái kèm phân trang
    Page<Member> findByStatus(Boolean status, Pageable pageable);

    // 2. Khai báo hàm phục vụ kiểm tra trùng Email
    boolean existsByEmail(String email);

    // 3. Số lượt mượn (số phiếu) của từng thành viên, kể cả thành viên chưa mượn lần nào
    @Query("SELECT new com.example.baitaptuan1.dto.MemberBorrowCount(" +
            "m.id, m.fullName, m.email, COUNT(b.id)) " +
            "FROM Member m LEFT JOIN Borrowing b ON b.member = m " +
            "GROUP BY m.id, m.fullName, m.email " +
            "ORDER BY COUNT(b.id) DESC, m.fullName ASC")
    List<MemberBorrowCount> countBorrowingsByMember();
}
