package com.example.samuraitravel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /** 民宿ごとのレビュー一覧（ページング） */
    Page<Review> findByHouse_Id(Integer houseId, Pageable pageable);

    /** 民宿 × ユーザ で1件（自分のレビュー取得） */
    Optional<Review> findByHouse_IdAndUser_Id(Integer houseId, Integer userId);

    /** レビューID × ユーザID で1件（所有者チェック） */
    Optional<Review> findByIdAndUser_Id(Integer id, Integer userId);

    /** ★ 民宿ごとのレビュー総件数 */
    long countByHouse_Id(Integer houseId);
}
