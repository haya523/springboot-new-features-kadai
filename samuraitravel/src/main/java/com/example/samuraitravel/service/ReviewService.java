package com.example.samuraitravel.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    /** 民宿ごとのレビュー一覧（ページング、ページ番号/件数指定） */
    @Transactional(readOnly = true)
    public Page<Review> getReviewsForHouse(Integer houseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reviewRepository.findByHouse_Id(houseId, pageable);
    }

    /** 互換用：コントローラから Pageable で渡す旧呼び出しに対応 */
    @Transactional(readOnly = true)
    public Page<Review> findPageForHouse(Integer houseId, Pageable pageable) {
        return reviewRepository.findByHouse_Id(houseId, pageable);
    }

    /** 民宿 × ユーザ で1件（自分のレビュー取得） */
    @Transactional(readOnly = true)
    public Optional<Review> getUserReviewForHouse(Integer houseId, Integer userId) {
        return reviewRepository.findByHouse_IdAndUser_Id(houseId, userId);
    }

    /** レビュー新規作成（同一ユーザが同一民宿へは1件まで） */
    public Review create(Integer houseId, Integer userId, ReviewForm form) {
        reviewRepository.findByHouse_IdAndUser_Id(houseId, userId)
                .ifPresent(r -> { throw new IllegalStateException("この民宿には既にレビュー済みです"); });

        Review r = new Review();
        r.setHouse(houseRepository.getReferenceById(houseId));
        r.setUser(userRepository.getReferenceById(userId));
        r.setRating(form.getRating());
        r.setComment(form.getComment());
        return reviewRepository.save(r);
    }

    /** 自分のレビューを Optional で返す（map()/orElse() で使える） */
    @Transactional(readOnly = true)
    public Optional<Review> findMyReview(Integer houseId, Integer userId) {
        return reviewRepository.findByHouse_IdAndUser_Id(houseId, userId);
    }

    /** 自分のレビュー1件を、所有者チェック付きで必ず取得 */
    @Transactional(readOnly = true)
    public Review getOwn(Integer id, Integer userId) {
        return reviewRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new AccessDeniedException("レビューの所有者ではありません"));
    }

    /** 更新（所有者チェック付き） */
    public Review update(Integer id, Integer userId, ReviewForm form) {
        Review r = getOwn(id, userId);
        r.setRating(form.getRating());
        r.setComment(form.getComment());
        return reviewRepository.save(r);
    }

    /** 削除（所有者チェック付き）。戻り値=民宿ID（リダイレクト用） */
    public Integer delete(Integer id, Integer userId) {
        Review r = getOwn(id, userId);
        Integer houseId = r.getHouse().getId();
        reviewRepository.delete(r);
        return houseId;
    }

    /** 民宿ごとのレビュー総件数 */
    @Transactional(readOnly = true)
    public long countForHouse(Integer houseId) {
        return reviewRepository.countByHouse_Id(houseId);
    }

    /** ReviewController 等で使う：民宿を必ず取得（見つからなければ 400） */
    @Transactional(readOnly = true)
    public House getHouseOrThrow(Integer houseId) {
        return houseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("指定の民宿が見つかりません: id=" + houseId));
    }
}
