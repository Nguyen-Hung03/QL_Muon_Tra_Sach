package com.example.baitaptuan1.controller;

import com.example.baitaptuan1.dto.BorrowLine;
import com.example.baitaptuan1.entity.Borrowing;
import com.example.baitaptuan1.entity.BorrowingStatus;
import com.example.baitaptuan1.exception.BusinessException;
import com.example.baitaptuan1.service.BookService;
import com.example.baitaptuan1.service.BorrowingService;
import com.example.baitaptuan1.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final MemberService memberService;
    private final BookService bookService;

    public BorrowingController(BorrowingService borrowingService, MemberService memberService, BookService bookService) {
        this.borrowingService = borrowingService;
        this.memberService = memberService;
        this.bookService = bookService;
    }

    @GetMapping
    public String list(@RequestParam(value = "status", required = false) BorrowingStatus status,
                       @RequestParam(value = "memberId", required = false) Long memberId,
                       @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                       @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                       @RequestParam(value = "overdueOnly", defaultValue = "false") boolean overdueOnly,
                       Model model) {
        model.addAttribute("borrowings", borrowingService.search(status, memberId, fromDate, toDate, overdueOnly));
        model.addAttribute("members", memberService.getAllMembers(null, Pageable.unpaged()).getContent());
        model.addAttribute("statuses", BorrowingStatus.values());
        model.addAttribute("status", status);
        model.addAttribute("memberId", memberId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("overdueOnly", overdueOnly);
        return "borrowing/list";
    }

    @GetMapping("/new")
    public String showBorrowForm(Model model) {
        prepareBorrowForm(model);
        return "borrowing/form";
    }

    @PostMapping
    public String createBorrowing(@RequestParam("memberId") Long memberId,
                                  @RequestParam("bookId") List<Long> bookIds,
                                  @RequestParam("quantity") List<Integer> quantities,
                                  @RequestParam(value = "dueDate", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            Borrowing borrowing = borrowingService.createBorrowing(memberId, toLines(bookIds, quantities), dueDate);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo phiếu mượn thành công.");
            return "redirect:/borrowings/" + borrowing.getId();
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            prepareBorrowForm(model);
            return "borrowing/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("borrowing", borrowingService.getBorrowing(id));
        model.addAttribute("details", borrowingService.getDetails(id));
        return "borrowing/detail";
    }

    @PostMapping("/{id}/return")
    public String returnBooks(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowingService.returnBorrowing(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã trả sách và hoàn số lượng vào kho.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/borrowings/" + id;
    }

    private void prepareBorrowForm(Model model) {
        model.addAttribute("members", memberService.getAllMembers(true, Pageable.unpaged()).getContent());
        model.addAttribute("books", bookService.getAllBooks(null, Pageable.unpaged()).getContent());
        model.addAttribute("defaultDueDate", LocalDateTime.now().plusDays(14).truncatedTo(ChronoUnit.MINUTES).toString().substring(0, 16));
    }

    private List<BorrowLine> toLines(List<Long> bookIds, List<Integer> quantities) {
        if (bookIds == null || quantities == null || bookIds.size() != quantities.size()) {
            throw new BusinessException("Danh sách sách và số lượng không khớp.");
        }
        List<BorrowLine> lines = new ArrayList<>();
        for (int i = 0; i < bookIds.size(); i++) {
            lines.add(new BorrowLine(bookIds.get(i), quantities.get(i)));
        }
        return lines;
    }
}
