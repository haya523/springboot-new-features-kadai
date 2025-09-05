package com.example.samuraitravel.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    public Page<Favorite> getPageForUser(Integer userId, Pageable pageable) {
        return favoriteRepository.findPageByUserIdWithHouse(userId, pageable);
    }

    public boolean isFavorite(Integer userId, Integer houseId) {
        return favoriteRepository.existsByUser_IdAndHouse_Id(userId, houseId);
    }

    @Transactional
    public void add(Integer userId, Integer houseId) {
        if (isFavorite(userId, houseId)) return;
        User u = new User(); u.setId(userId);
        House h = new House(); h.setId(houseId);
        favoriteRepository.save(new Favorite(u, h));
    }

    @Transactional
    public void remove(Integer userId, Integer houseId) {
        favoriteRepository.deleteByUser_IdAndHouse_Id(userId, houseId);
    }
}
