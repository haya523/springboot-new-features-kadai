package com.example.samuraitravel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUser_IdAndHouse_Id(Integer userId, Integer houseId);

    Optional<Favorite> findByUser_IdAndHouse_Id(Integer userId, Integer houseId);

    Page<Favorite> findByUser_IdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    long countByUser_Id(Integer userId);

    void deleteByUser_IdAndHouse_Id(Integer userId, Integer houseId);
}
