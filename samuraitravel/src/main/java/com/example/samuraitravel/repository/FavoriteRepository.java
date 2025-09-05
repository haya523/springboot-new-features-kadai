package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    // 一覧用：House を fetch join（LazyInitializationException 回避）
    @Query(
        value = "SELECT f FROM Favorite f JOIN FETCH f.house h WHERE f.user.id = :userId ORDER BY f.createdAt DESC",
        countQuery = "SELECT COUNT(f) FROM Favorite f WHERE f.user.id = :userId"
    )
    Page<Favorite> findPageByUserIdWithHouse(@Param("userId") Integer userId, Pageable pageable);

    boolean existsByUser_IdAndHouse_Id(Integer userId, Integer houseId);

    void deleteByUser_IdAndHouse_Id(Integer userId, Integer houseId);
}
