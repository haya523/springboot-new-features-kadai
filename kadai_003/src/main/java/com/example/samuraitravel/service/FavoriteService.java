package com.example.samuraitravel.service;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    /** すでにお気に入り済みか */
    public boolean isFavorited(Integer userId, Integer houseId) {
        return favoriteRepository.existsByUser_IdAndHouse_Id(userId, houseId);
    }

    /** トグル：お気に入り → 未登録なら追加 / 登録済みなら削除。戻り値は「登録後の状態」 */
    public boolean toggle(Integer userId, Integer houseId) {
        if (isFavorited(userId, houseId)) {
            favoriteRepository.deleteByUser_IdAndHouse_Id(userId, houseId);
            return false; // 解除後
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("user not found: " + userId));
            House house = houseRepository.findById(houseId)
                    .orElseThrow(() -> new NoSuchElementException("house not found: " + houseId));

            Favorite f = new Favorite();
            f.setUser(user);
            f.setHouse(house);
            try {
                favoriteRepository.save(f);
            } catch (DataIntegrityViolationException e) {
                // 競合でユニーク制約に引っかかった時は既に登録とみなす
                return true;
            }
            return true; // 追加後
        }
    }

    /** 自分のお気に入り一覧（新しい順） */
    public Page<Favorite> listMy(Integer userId, Pageable pageable) {
        return favoriteRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);
    }

    public long countMy(Integer userId) {
        return favoriteRepository.countByUser_Id(userId);
    }
}
