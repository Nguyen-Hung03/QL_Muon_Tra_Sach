package com.example.baitaptuan1.service;

import com.example.baitaptuan1.dto.BorrowLine;
import com.example.baitaptuan1.entity.Book;
import com.example.baitaptuan1.entity.Borrowing;
import com.example.baitaptuan1.entity.BorrowingStatus;
import com.example.baitaptuan1.entity.Member;
import com.example.baitaptuan1.exception.BusinessException;
import com.example.baitaptuan1.repository.BookRepository;
import com.example.baitaptuan1.repository.BorrowingRepository;
import com.example.baitaptuan1.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class BorrowingServiceTest {

    @Autowired
    private BorrowingService borrowingService;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BorrowingRepository borrowingRepository;

    private static final AtomicInteger sequence = new AtomicInteger();

    @Test
    void borrowSeveralBooksReducesStock() {
        Member member = member(true);
        Book first = book(5);
        Book second = book(4);
        borrowingService.createBorrowing(member.getId(), List.of(
                new BorrowLine(first.getId(), 2),
                new BorrowLine(second.getId(), 1)
        ), LocalDateTime.now().plusDays(7));

        assertEquals(0, reload(first).getAvailableQuantity().compareTo(new BigDecimal("3")));
        assertEquals(0, reload(second).getAvailableQuantity().compareTo(new BigDecimal("3")));
    }

    @Test
    void oneBookOutOfStockRollsBackTheWholeSlip() {
        Member member = member(true);
        Book enough = book(5);
        Book empty = book(0);
        assertThrows(BusinessException.class, () -> borrowingService.createBorrowing(member.getId(), List.of(
                new BorrowLine(enough.getId(), 2),
                new BorrowLine(empty.getId(), 1)
        ), LocalDateTime.now().plusDays(7)));

        assertEquals(0, reload(enough).getAvailableQuantity().compareTo(new BigDecimal("5")));
        assertEquals(0, reload(empty).getAvailableQuantity().compareTo(BigDecimal.ZERO));
    }

    @Test
    void returnRestoresStock() {
        Member member = member(true);
        Book book = book(3);
        Borrowing borrowing = borrowingService.createBorrowing(member.getId(),
                List.of(new BorrowLine(book.getId(), 1)), LocalDateTime.now().plusDays(2));

        borrowingService.returnBorrowing(borrowing.getId());

        Borrowing saved = borrowingRepository.findById(borrowing.getId()).orElseThrow();
        assertEquals(BorrowingStatus.RETURNED, saved.getStatus());
        assertEquals(0, reload(book).getAvailableQuantity().compareTo(new BigDecimal("3")));
    }

    @Test
    void blockedMemberCannotBorrow() {
        Member member = member(false);
        Book book = book(2);
        assertThrows(BusinessException.class, () -> borrowingService.createBorrowing(member.getId(),
                List.of(new BorrowLine(book.getId(), 1)), LocalDateTime.now().plusDays(7)));
        assertEquals(0, reload(book).getAvailableQuantity().compareTo(new BigDecimal("2")));
    }

    private Member member(boolean active) {
        int n = sequence.incrementAndGet();
        Member member = new Member();
        member.setFullName("Độc giả " + n);
        member.setEmail("reader" + n + "@mail.test");
        member.setPhone("090000" + n);
        member.setStatus(active);
        return memberRepository.save(member);
    }

    private Book book(int quantity) {
        int n = sequence.incrementAndGet();
        Book book = new Book();
        book.setIsbn("ISBN-" + n);
        book.setTitle("Sách " + n);
        book.setAuthor("Tác giả");
        book.setTotalQuantity(BigDecimal.valueOf(quantity));
        book.setAvailableQuantity(BigDecimal.valueOf(quantity));
        book.setStatus(true);
        return bookRepository.save(book);
    }

    private Book reload(Book book) {
        return bookRepository.findById(book.getId()).orElseThrow();
    }
}
