package com.example.baitaptuan1.repository;

import com.example.baitaptuan1.dto.TopBorrowedBook;
import com.example.baitaptuan1.entity.BorrowingDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, Long> {

    @Query("SELECT d FROM BorrowingDetail d JOIN FETCH d.book WHERE d.borrowing.id = :borrowingId ORDER BY d.id ASC")
    List<BorrowingDetail> findByBorrowingIdFetchBook(Long borrowingId);

    @Query("SELECT new com.example.baitaptuan1.dto.TopBorrowedBook(" +
            "b.id, b.isbn, b.title, b.author, SUM(d.quantity)) " +
            "FROM BorrowingDetail d JOIN d.book b " +
            "GROUP BY b.id, b.isbn, b.title, b.author " +
            "ORDER BY SUM(d.quantity) DESC, b.title ASC")
    List<TopBorrowedBook> findTopBorrowedBooks(Pageable pageable);
}
