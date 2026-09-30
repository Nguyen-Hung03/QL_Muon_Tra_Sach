package com.example.baitaptuan1.controller;

import com.example.baitaptuan1.entity.Member;
import com.example.baitaptuan1.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**

     * Route 1: Màn hình danh sách thành viên (Mục 6 trong đề bài)
     * URL: GET /members?status=...&page=...&size=...
     * Yêu cầu đề bài: Có lọc theo trạng thái, hỗ trợ phân trang và sắp xếp mặc định theo ngày tạo (createdAt) giảm dần
     */
    @GetMapping
    public String listMembers(
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

// Cấu hình phân trang: Sắp xếp theo ngày tạo (createdAt) giảm dần để thành viên mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

// Gọi service lấy danh sách kèm bộ lọc trạng thái
        Page<Member> memberPage = memberService.getAllMembers(status, pageable);

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("statusFilter", status); // Giữ lại trạng thái đang lọc để bôi đậm nút trên giao diện

        return "member/list"; // Trỏ đến file templates/member/list.html
    }

    /**

     * Route 2: Màn hình Form thêm thành viên mới (Mục 6 trong đề bài)
     * URL: GET /members/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        return "member/form"; // Trỏ đến file templates/member/form.html
    }

    /**

     * Route 3: Màn hình Form chỉnh sửa thông tin thành viên (Mục 6 trong đề bài)
     * URL: GET /members/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Member member = memberService.getMemberById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + id));

        model.addAttribute("member", member);
        return "member/form";
    }

    /**

     * Route 4: Xử lý Lưu thành viên khi bấm Submit Form (Thêm mới / Cập nhật)
     * URL: POST /members/save
     */
    @PostMapping("/save")
    public String saveMember(
            @Valid @ModelAttribute("member") Member member,
            BindingResult result,
            Model model) {

// 1. Kiểm tra các lỗi Validation đặt tại Entity (như @NotBlank ở tên, @Email ở định dạng email)
        if (result.hasErrors()) {
            return "member/form";
        }

        try {
// 2. Gọi service xử lý lưu dữ liệu (có cơ chế tự động chặn trùng Email)
            memberService.saveMember(member);
        } catch (IllegalArgumentException e) {
// 3. Nếu dính lỗi trùng email, trả lại thông tin báo lỗi về Form
            model.addAttribute("errorMessage", e.getMessage());
            return "member/form";
        }

        return "redirect:/members"; // Lưu thành công, quay về danh sách thành viên
    }

    /**

     * Route 5: Xử lý hành động Khóa / Mở khóa thành viên (Mục 4.3 trong đề bài)
     * URL: GET /members/{id}/toggle-status
     */
    @GetMapping("/{id}/toggle-status")
    public String toggleMemberStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
// Gọi hàm đảo ngược trạng thái tài khoản (Active <-> Blocked)
            memberService.toggleMemberStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/members"; // Xử lý xong quay về trang danh sách
    }

}