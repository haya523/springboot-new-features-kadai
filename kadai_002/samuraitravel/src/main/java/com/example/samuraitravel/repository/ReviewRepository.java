package com.example.samuraitravel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    Page<Review> findByHouse_Id(Integer houseId, Pageable pageable);

    Optional<Review> findByHouse_IdAndUser_Id(Integer houseId, Integer userId);

    Optional<Review> findByIdAndUser_Id(Integer id, Integer userId);
}
