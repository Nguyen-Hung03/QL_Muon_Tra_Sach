package com.example.baitaptuan1.service;

import com.example.baitaptuan1.entity.Book;
import com.example.baitaptuan1.entity.Category;
import com.example.baitaptuan1.repository.BookRepository;
import com.example.baitaptuan1.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Book> getAllBooks(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return bookRepository.search(keyword,pageable);
        }
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long id){
        return bookRepository.findById(id);
    }

    @Transactional
    public Book saveBook(Book book, Long categoryId) {
// 1. Kiểm tra quy tắc nghiệp vụ số lượng (available_quantity không được nhỏ hơn 0 hoặc lớn hơn total_quantity)
        if (book.getTotalQuantity() == null || book.getAvailableQuantity() == null) {
            throw new IllegalArgumentException("Số lượng tổng và số lượng hiện có không được để trống.");
        }
        if (book.getAvailableQuantity().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Số lượng sách hiện có không được nhỏ hơn 0.");
        }
        if (book.getAvailableQuantity().compareTo(book.getTotalQuantity()) > 0) {
            throw new IllegalArgumentException("Số lượng sách hiện có không được lớn hơn tổng số lượng sách.");
        }
        // 2. Kiểm tra trùng mã ISBN để đảm bảo tính duy nhất
        if (book.getId() == null) { // Thêm mới
            if (bookRepository.existsByIsbn(book.getIsbn())) {
                throw new IllegalArgumentException("Mã ISBN này đã tồn tại trong hệ thống.");
            }
        } else { // Sửa sách
            Optional<Book> existingBook = bookRepository.findById(book.getId());
            if (existingBook.isPresent() && !existingBook.get().getIsbn().equals(book.getIsbn())) {
                if (bookRepository.existsByIsbn(book.getIsbn())) {
                    throw new IllegalArgumentException("Mã ISBN mới đã bị trùng với một cuốn sách khác.");
                }
            }
        }// 3. Liên kết Sách với Thể loại (Category) thông qua CategoryRepository
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Thể loại sách không tồn tại với ID: " + categoryId));
            book.setCategory(category);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sách cần xóa."));

        // QUY TẮC: Không cho xóa sách nếu sách đang có lượt mượn chưa trả
        // Cách 1: Nếu bạn thiết kế sẵn trường available_quantity và total_quantity:
        // Nếu số lượng hiện có nhỏ hơn tổng số lượng sách, nghĩa là có người đang cầm sách đi mượn chưa trả!
        if (book.getAvailableQuantity().compareTo(book.getTotalQuantity()) < 0) {
            throw new IllegalStateException("Không thể xóa cuốn sách này vì hiện tại đang có lượt mượn chưa trả!");
        }

        bookRepository.delete(book);
    }
}
