package com.example.baitaptuan1.controller;

import com.example.baitaptuan1.dto.MemberBorrowCount;
import com.example.baitaptuan1.dto.TopBorrowedBook;
import com.example.baitaptuan1.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final BorrowingService borrowingService;

    @Autowired
    public ReportController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    /**
     * Mục 4.6: Trang chọn một trong ba báo cáo đơn giản.
     * URL: GET /reports
     */
    @GetMapping
    public String reportHome() {
        return "report/index";
    }

    /**
     * Danh sách phiếu mượn quá hạn.
     * URL: GET /reports/overdue
     */
    @GetMapping("/overdue")
    public String overdueReport(Model model) {
        model.addAttribute("reportList", borrowingService.getOverdueReport());
        return "report/overdue";
    }

    /**
     * Top 5 sách được mượn nhiều nhất.
     * URL: GET /reports/top-books
     */
    @GetMapping("/top-books")
    public String topBorrowedBooks(Model model) {
        List<TopBorrowedBook> topBooks = borrowingService.getTopBorrowedBooks();
        model.addAttribute("topBooks", topBooks);
        return "report/top-books";
    }

    /**
     * Số lượt mượn theo thành viên.
     * URL: GET /reports/by-member
     */
    @GetMapping("/by-member")
    public String borrowCountByMember(Model model) {
        List<MemberBorrowCount> memberStats = borrowingService.getBorrowCountByMember();
        model.addAttribute("memberStats", memberStats);
        return "report/by-member";
    }
}
