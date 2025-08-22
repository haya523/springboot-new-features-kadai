package com.example.samuraitravel.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Page<Review> getReviewsForHouse(Integer houseId, int page, int size) {
        // createdAt があるなら Sort.by("createdAt").descending() に変えてOK
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return reviewRepository.findByHouseId(houseId, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Review> getUserReviewForHouse(Integer houseId, Integer userId) {
        return reviewRepository.findByHouseIdAndUserId(houseId, userId);
    }

    public Review create(Integer houseId, Integer userId, ReviewForm form) {
        // 二重投稿防止
        reviewRepository.findByHouseIdAndUserId(houseId, userId)
                .ifPresent(r -> { throw new IllegalStateException("この民宿には既にレビュー済みです"); });

        Review r = new Review();
        r.setHouse(houseRepository.getReferenceById(houseId));
        r.setUser(userRepository.getReferenceById(userId));
        r.setRating(form.getRating());
        r.setComment(form.getComment());
        return reviewRepository.save(r);
    }

    @Transactional(readOnly = true)
    public Review getOwn(Integer id, Integer userId) {
        return reviewRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AccessDeniedException("レビューの所有者ではありません"));
    }

    public Review update(Integer id, Integer userId, ReviewForm form) {
        Review r = getOwn(id, userId);
        r.setRating(form.getRating());
        r.setComment(form.getComment());
        return reviewRepository.save(r);
    }

    /** 戻り値に houseId を返すとリダイレクトに便利 */
    public Integer delete(Integer id, Integer userId) {
        Review r = getOwn(id, userId);
        Integer houseId = r.getHouse().getId();
        reviewRepository.delete(r);
        return houseId;
    }
}
