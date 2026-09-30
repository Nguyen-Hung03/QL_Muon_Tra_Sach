package com.example.baitaptuan1.seeder;

import com.example.baitaptuan1.entity.Book;
import com.example.baitaptuan1.entity.Borrowing;
import com.example.baitaptuan1.entity.BorrowingDetail;
import com.example.baitaptuan1.entity.BorrowingStatus;
import com.example.baitaptuan1.entity.Category;
import com.example.baitaptuan1.entity.Member;
import com.example.baitaptuan1.repository.BookRepository;
import com.example.baitaptuan1.repository.BorrowingRepository;
import com.example.baitaptuan1.repository.CategoryRepository;
import com.example.baitaptuan1.repository.MemberRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LibraryDataSeeder implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRepository borrowingRepository;

    public LibraryDataSeeder(CategoryRepository categoryRepository, BookRepository bookRepository,
                             MemberRepository memberRepository, BorrowingRepository borrowingRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowingRepository = borrowingRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category literature = category("Văn học", "Tiểu thuyết và truyện ngắn");
        Category science = category("Khoa học", "Khoa học thường thức");
        Category history = category("Lịch sử", "Lịch sử Việt Nam và thế giới");
        Category children = category("Thiếu nhi", "Sách cho lứa tuổi nhỏ");
        Category technology = category("Công nghệ", "Tin học và lập trình");

        List<Book> books = List.of(
                book("VH-001", "Truyện ngắn chọn lọc", "Nguyễn Nhật", literature, 12),
                book("VH-002", "Nhà văn và thời gian", "Trần Mai", literature, 8),
                book("VH-003", "Bước chân mùa thu", "Lê Hoa", literature, 10),
                book("VH-004", "Gió heo may", "Phạm Lan", literature, 9),
                book("VH-005", "Dòng sông tuổi trẻ", "Hoàng Sơn", literature, 11),
                book("KH-001", "Vũ trụ gần", "Đỗ Quang", science, 7),
                book("KH-002", "Sinh học đại cương", "Vũ Hà", science, 15),
                book("KH-003", "Hóa học vui", "Bùi Nam", science, 10),
                book("KH-004", "Vật lý đời thường", "Ngô Khoa", science, 6),
                book("LS-001", "Sử Việt tóm tắt", "Lý Minh", history, 14),
                book("LS-002", "Thế giới thế kỷ 20", "Đặng Phúc", history, 8),
                book("LS-003", "Thành Thăng Long", "Mai Chi", history, 9),
                book("LS-004", "Biển Đông xưa", "Phan An", history, 5),
                book("TN-001", "Chú thỏ học bài", "Kim Anh", children, 20),
                book("TN-002", "Khu vườn bí mật nhỏ", "Hà My", children, 12),
                book("TN-003", "Những người bạn", "Trịnh Tú", children, 16),
                book("TN-004", "Cây kẹo mút", "Lâm Vy", children, 18),
                book("CN-001", "Nhập môn lập trình", "Quốc Bảo", technology, 10),
                book("CN-002", "Cơ sở dữ liệu", "Thúy Nga", technology, 8),
                book("CN-003", "Mạng máy tính", "Hữu Phước", technology, 7),
                book("CN-004", "Spring Boot thực hành", "Minh Khang", technology, 6),
                book("CN-005", "Thuật toán căn bản", "Gia Hân", technology, 9)
        );
        books.get(21).setStatus(false);
        bookRepository.save(books.get(21));

        Member an = member("Nguyễn An", "an.nguyen@mail.test", "0901000001", true);
        Member binh = member("Trần Bình", "binh.tran@mail.test", "0901000002", true);
        Member chi = member("Lê Chi", "chi.le@mail.test", "0901000003", true);
        member("Phạm Dũng", "dung.pham@mail.test", "0901000004", false);
        Member em = member("Hoàng Em", "em.hoang@mail.test", "0901000005", true);
        member("Vũ Giang", "giang.vu@mail.test", "0901000006", true);
        member("Đỗ Hà", "ha.do@mail.test", "0901000007", false);
        member("Bùi Khoa", "khoa.bui@mail.test", "0901000008", true);
        member("Ngô Lan", "lan.ngo@mail.test", "0901000009", true);
        member("Lý Minh", "minh.ly@mail.test", "0901000010", false);

        LocalDateTime now = LocalDateTime.now();
        openLoan(an, books.get(0), 1, now.minusDays(20), now.minusDays(6), BorrowingStatus.OVERDUE);
        returnedLoan(binh, books.get(1), 1, now.minusDays(30), now.minusDays(16), now.minusDays(18));
        openLoan(chi, books.get(2), 2, now.minusDays(2), now.plusDays(12), BorrowingStatus.BORROWING);
        openLoan(em, books.get(5), 1, now.minusDays(1), now.plusDays(13), BorrowingStatus.BORROWING);
        returnedLoan(an, books.get(13), 1, now.minusDays(40), now.minusDays(26), now.minusDays(28));
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(true);
        return categoryRepository.save(category);
    }

    private Book book(String isbn, String title, String author, Category category, int quantity) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setTotalQuantity(BigDecimal.valueOf(quantity));
        book.setAvailableQuantity(BigDecimal.valueOf(quantity));
        book.setStatus(true);
        return bookRepository.save(book);
    }

    private Member member(String fullName, String email, String phone, boolean active) {
        Member member = new Member();
        member.setFullName(fullName);
        member.setEmail(email);
        member.setPhone(phone);
        member.setStatus(active);
        return memberRepository.save(member);
    }

    private void openLoan(Member member, Book book, int quantity, LocalDateTime borrowDate, LocalDateTime dueDate, BorrowingStatus status) {
        book.setAvailableQuantity(book.getAvailableQuantity().subtract(BigDecimal.valueOf(quantity)));
        bookRepository.save(book);
        Borrowing borrowing = baseLoan(member, borrowDate, dueDate, status);
        borrowing.getBorrowingDetails().add(detail(borrowing, book, quantity));
        borrowingRepository.save(borrowing);
    }

    private void returnedLoan(Member member, Book book, int quantity, LocalDateTime borrowDate, LocalDateTime dueDate, LocalDateTime returnedDate) {
        Borrowing borrowing = baseLoan(member, borrowDate, dueDate, BorrowingStatus.RETURNED);
        borrowing.setReturnedDate(returnedDate);
        borrowing.getBorrowingDetails().add(detail(borrowing, book, quantity));
        borrowingRepository.save(borrowing);
    }

    private Borrowing baseLoan(Member member, LocalDateTime borrowDate, LocalDateTime dueDate, BorrowingStatus status) {
        Borrowing borrowing = new Borrowing();
        borrowing.setMember(member);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(dueDate);
        borrowing.setStatus(status);
        return borrowing;
    }

    private BorrowingDetail detail(Borrowing borrowing, Book book, int quantity) {
        BorrowingDetail detail = new BorrowingDetail();
        detail.setBorrowing(borrowing);
        detail.setBook(book);
        detail.setQuantity(quantity);
        return detail;
    }
}
