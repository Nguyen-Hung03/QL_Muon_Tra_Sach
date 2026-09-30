package com.example.baitaptuan1.repository;

import com.example.baitaptuan1.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Tên hàm siêu ngắn, truyền duy nhất 1 biến keyword
    @Query("SELECT b FROM Book b LEFT JOIN b.category c WHERE " +
                  "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                  "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                  "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                  "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> search(String keyword, Pageable pageable);

    boolean existsByIsbn(String isbn);
}
