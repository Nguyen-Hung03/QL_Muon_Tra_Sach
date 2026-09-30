package com.example.baitaptuan1.repository;

import com.example.baitaptuan1.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BorrowingRepository extends JpaRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {

    @Query("SELECT DISTINCT b FROM Borrowing b JOIN FETCH b.member " +
            "WHERE b.dueDate < :now AND b.status <> com.example.baitaptuan1.entity.BorrowingStatus.RETURNED " +
            "ORDER BY b.dueDate ASC")
    List<Borrowing> findOverdueBorrowings(LocalDateTime now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Borrowing b SET b.status = com.example.baitaptuan1.entity.BorrowingStatus.OVERDUE " +
            "WHERE b.dueDate < :now AND b.status = com.example.baitaptuan1.entity.BorrowingStatus.BORROWING")
    int markOverdue(LocalDateTime now);

    @Query("SELECT b FROM Borrowing b JOIN FETCH b.member WHERE b.id = :id")
    Optional<Borrowing> findWithMemberById(Long id);
}
