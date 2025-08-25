package com.example.samuraitravel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    // 民宿（house の id）でページング取得
    Page<Review> findByHouse_Id(Integer houseId, Pageable pageable);

    // 特定の民宿 + 特定のユーザーのレビューを1件
    Optional<Review> findByHouse_IdAndUser_Id(Integer houseId, Integer userId);

    // 自分のレビュー1件を id 指定 + 所有者チェック付きで取得
    Optional<Review> findByIdAndUser_Id(Integer id, Integer userId);
}
