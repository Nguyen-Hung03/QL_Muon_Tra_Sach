package com.example.baitaptuan1.controller;

import com.example.baitaptuan1.entity.Book;
import com.example.baitaptuan1.entity.Category;
import com.example.baitaptuan1.service.BookService;
import com.example.baitaptuan1.service.CategoryService;
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

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @Autowired
    public BookController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    /**

     * Route 1: Màn hình danh sách sách (Mục 6 trong đề bài)
     * URL: GET /books?keyword=...&page=...&size=...
     */
    @GetMapping
    public String listBooks(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

// Cấu hình phân trang: Mặc định sắp xếp theo id tăng dần (hoặc bạn có thể đổi sang trường khác)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

// Gọi service tìm kiếm tổng hợp đã hoàn thiện ở các bước trước
        Page<Book> bookPage = bookService.getAllBooks(keyword, pageable);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "book/list"; // Trỏ đến file templates/book/list.html
    }

    /**

     * Route 2: Màn hình Form thêm sách mới (Mục 6 trong đề bài)
     * URL: GET /books/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());

// Nạp tất cả thể loại từ CategoryService đổ vào dropdown select ngoài giao diện HTML
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "book/form"; // Trỏ đến file templates/book/form.html (dùng chung cho cả thêm và sửa)
    }

    /**

     * Route 3: Màn hình Form chỉnh sửa sách (Mục 6 trong đề bài)
     * URL: GET /books/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách với ID: " + id));

        model.addAttribute("book", book);

// Đổ danh sách thể loại phục vụ sửa danh mục sách
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "book/form";
    }

    /**

     * Route 4: Xử lý Lưu sách khi bấm Submit Form (Thêm mới / Cập nhật)
     * URL: POST /books/save
     */
    @PostMapping
    public String saveBook(
            @Valid @ModelAttribute("book") Book book,
            BindingResult result,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {

// 1. Nếu vi phạm các validation cơ bản đã đặt ở Entity (như @NotBlank ở title)
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/form";
        }

        try {
// 2. Gọi hàm lưu sách kết hợp truyền ID thể loại để kết nối bảng trung gian
            bookService.saveBook(book, categoryId);
        } catch (IllegalArgumentException e) {
// 3. Nếu dính lỗi trùng mã ISBN hoặc sai logic số lượng sách, ném lỗi ngược lại giao diện hiển thị báo lỗi
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "book/form";
        }

        return "redirect:/books"; // Lưu thành công, quay về trang danh sách
    }

    /**

     * Route 5: Xử lý hành động Xóa sách
     * URL: GET /books/{id}/delete hoặc dùng POST tùy cấu hình giao diện của bạn
     */
    @DeleteMapping("/{id}") // URL thực tế sẽ là: DELETE /books/1, DELETE /books/2...
    public String deleteBook(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm xóa sách (có cơ chế chặn xóa nếu sách đang được mượn)
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
        } catch (Exception e) {
            // Nếu chặn thành công do sách đang có lượt mượn, bắn tin nhắn cảnh báo ra màn hình
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books";
    }

}
