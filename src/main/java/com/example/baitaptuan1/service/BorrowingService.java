package com.example.baitaptuan1.service;

import com.example.baitaptuan1.dto.BorrowLine;
import com.example.baitaptuan1.dto.MemberBorrowCount;
import com.example.baitaptuan1.dto.OverdueReportRow;
import com.example.baitaptuan1.dto.TopBorrowedBook;
import com.example.baitaptuan1.entity.Book;
import com.example.baitaptuan1.entity.Borrowing;
import com.example.baitaptuan1.entity.BorrowingDetail;
import com.example.baitaptuan1.entity.BorrowingStatus;
import com.example.baitaptuan1.entity.Member;
import com.example.baitaptuan1.exception.BusinessException;
import com.example.baitaptuan1.repository.BookRepository;
import com.example.baitaptuan1.repository.BorrowingDetailRepository;
import com.example.baitaptuan1.repository.BorrowingRepository;
import com.example.baitaptuan1.repository.BorrowingSpecifications;
import com.example.baitaptuan1.repository.MemberRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public BorrowingService(BorrowingRepository borrowingRepository,
                            BorrowingDetailRepository borrowingDetailRepository,
                            BookRepository bookRepository,
                            MemberRepository memberRepository) {
        this.borrowingRepository = borrowingRepository;
        this.borrowingDetailRepository = borrowingDetailRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Borrowing createBorrowing(Long memberId, List<BorrowLine> lines, LocalDateTime dueDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException("Thành viên không tồn tại."));
        if (Boolean.FALSE.equals(member.getStatus())) {
            throw new BusinessException("Tài khoản thành viên này đã bị khóa. Không thể mượn sách.");
        }
        if (lines == null || lines.isEmpty()) {
            throw new BusinessException("Phiếu mượn phải chứa ít nhất một đầu sách.");
        }

        LocalDateTime borrowDate = LocalDateTime.now();
        LocalDateTime resolvedDue = dueDate == null ? borrowDate.plusDays(14) : dueDate;
        if (!resolvedDue.isAfter(borrowDate)) {
            throw new BusinessException("Hạn trả phải sau thời điểm mượn.");
        }

        Set<Long> seen = new HashSet<>();
        Borrowing borrowing = new Borrowing();
        borrowing.setMember(member);
        borrowing.setBorrowDate(borrowDate);
        borrowing.setDueDate(resolvedDue);
        borrowing.setStatus(BorrowingStatus.BORROWING);

        for (BorrowLine line : lines) {
            if (line.getBookId() == null) {
                throw new BusinessException("Thiếu mã sách trong phiếu mượn.");
            }
            if (!seen.add(line.getBookId())) {
                throw new BusinessException("Không được có hai dòng trùng một cuốn sách.");
            }
            if (line.getQuantity() == null || line.getQuantity() <= 0) {
                throw new BusinessException("Số lượng mượn phải lớn hơn 0.");
            }
            Book book = bookRepository.findById(line.getBookId())
                    .orElseThrow(() -> new BusinessException("Sách với ID " + line.getBookId() + " không tồn tại."));
            if (Boolean.FALSE.equals(book.getStatus())) {
                throw new BusinessException("Sách '" + book.getTitle() + "' đang ngừng phục vụ.");
            }
            BigDecimal requested = BigDecimal.valueOf(line.getQuantity());
            if (book.getAvailableQuantity() == null || book.getAvailableQuantity().compareTo(requested) < 0) {
                throw new BusinessException("Sách '" + book.getTitle() + "' không đủ số lượng trong kho để mượn.");
            }
            book.setAvailableQuantity(book.getAvailableQuantity().subtract(requested));
            bookRepository.save(book);

            BorrowingDetail detail = new BorrowingDetail();
            detail.setBorrowing(borrowing);
            detail.setBook(book);
            detail.setQuantity(line.getQuantity());
            borrowing.getBorrowingDetails().add(detail);
        }
        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBorrowing(Long id) {
        Borrowing borrowing = borrowingRepository.findWithMemberById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu mượn."));
        if (borrowing.getStatus() == BorrowingStatus.RETURNED) {
            throw new BusinessException("Phiếu này đã được trả.");
        }
        List<BorrowingDetail> details = borrowingDetailRepository.findByBorrowingIdFetchBook(id);
        for (BorrowingDetail detail : details) {
            Book book = bookRepository.findById(detail.getBook().getId())
                    .orElseThrow(() -> new BusinessException("Không tìm thấy sách."));
            book.setAvailableQuantity(book.getAvailableQuantity().add(BigDecimal.valueOf(detail.getQuantity())));
            bookRepository.save(book);
        }
        borrowing.setStatus(BorrowingStatus.RETURNED);
        borrowing.setReturnedDate(LocalDateTime.now());
        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public List<Borrowing> search(BorrowingStatus status, Long memberId, LocalDate fromDate, LocalDate toDate, boolean overdueOnly) {
        borrowingRepository.markOverdue(LocalDate.now().atStartOfDay());
        LocalDateTime from = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime to = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
        return borrowingRepository.findAll(BorrowingSpecifications.filter(status, memberId, from, to, overdueOnly));
    }

    @Transactional(readOnly = true)
    public Borrowing getBorrowing(Long id) {
        return borrowingRepository.findWithMemberById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu mượn."));
    }

    @Transactional(readOnly = true)
    public List<BorrowingDetail> getDetails(Long borrowingId) {
        return borrowingDetailRepository.findByBorrowingIdFetchBook(borrowingId);
    }

    @Transactional
    public List<OverdueReportRow> getOverdueReport() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        borrowingRepository.markOverdue(startOfToday);
        return borrowingRepository.findOverdueBorrowings(startOfToday).stream()
                .map(borrowing -> new OverdueReportRow(
                        borrowing.getId(),
                        borrowing.getMember().getFullName(),
                        borrowing.getBorrowDate(),
                        borrowing.getDueDate(),
                        ChronoUnit.DAYS.between(borrowing.getDueDate().toLocalDate(), LocalDate.now()),
                        borrowing.getStatus()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopBorrowedBook> getTopBorrowedBooks() {
        return borrowingDetailRepository.findTopBorrowedBooks(PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<MemberBorrowCount> getBorrowCountByMember() {
        return memberRepository.countBorrowingsByMember();
    }
}
