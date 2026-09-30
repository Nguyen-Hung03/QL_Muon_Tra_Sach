package com.example.baitaptuan1.service;

import com.example.baitaptuan1.entity.Member;
import com.example.baitaptuan1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    /**
     * Nghiệp vụ 1: Xem danh sách thành viên (Mục 4.3 & Mục 6)
     * Hỗ trợ lọc theo trạng thái (status) và tự động phân trang, sắp xếp
     */
    @Transactional(readOnly = true)
    public Page<Member> getAllMembers(Boolean status, Pageable pageable) {
// Nếu người dùng chọn lọc theo trạng thái (Active/Blocked)
        if (status != null) {
            return memberRepository.findByStatus(status, pageable);
        }
// Nếu không chọn lọc, hiển thị tất cả thành viên kèm phân trang
        return memberRepository.findAll(pageable);
    }
    /**
     * Lấy thông tin chi tiết thành viên theo ID
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }
    /**
     * Nghiệp vụ 2: Tạo mới hoặc Cập nhật thành viên (Mục 4.3)
     * Kiểm tra quy tắc: Email không được phép trùng lặp
     */
    @Transactional
    public Member saveMember(Member member) {
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email thành viên không được để trống.");
        }
// Kiểm tra trùng Email khi Thêm mới
        if (member.getId() == null) { // kiểm tra xem Id đã tồn tại chưa nếu chưa là create
            // nếu có thi là update
            if (memberRepository.existsByEmail(member.getEmail())) {
                throw new IllegalArgumentException("Email này đã được sử dụng bởi thành viên khác.");
            }
        } else {
// Kiểm tra trùng Email khi Cập nhật (Sửa) thành viên
            Optional<Member> existingMember = memberRepository.findById(member.getId());
            if (existingMember.isPresent() && !existingMember.get().getEmail().equalsIgnoreCase(member.getEmail())) {
                if (memberRepository.existsByEmail(member.getEmail())) {
                    throw new IllegalArgumentException("Email cập nhật đã bị trùng với thành viên khác.");
                }
            }
        }

        return memberRepository.save(member);
    }

    /**

     * Nghiệp vụ 3: Thay đổi trạng thái - Khóa hoặc Mở khóa thành viên (Mục 4.3)
     * Trạng thái true: ACTIVE (Hoạt động), false: BLOCKED (Bị khóa)
     */
    @Transactional
    public void toggleMemberStatus(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + id));

// Đảo ngược trạng thái hiện tại (Đang hoạt động -> Khóa và ngược lại)
        member.setStatus(!member.getStatus());
        memberRepository.save(member);
    }

    /**

     * Nghiệp vụ 4: Kiểm tra điều kiện mượn sách của thành viên (Mục 4.3 & Mục 7)
     * Thảy ra lỗi nếu thành viên bị BLOCKED (status = false)
     */
    @Transactional(readOnly = true)
    public void validateMemberForBorrowing(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Thành viên không tồn tại."));

// Quy tắc: Thành viên bị khóa (status = false) không được tạo phiếu mượn mới
        if (Boolean.FALSE.equals(member.getStatus())) {
            throw new IllegalStateException("Tài khoản thành viên này đã bị KHÓA (BLOCKED). Không thể tạo phiếu mượn mới!");
        }
    }

}