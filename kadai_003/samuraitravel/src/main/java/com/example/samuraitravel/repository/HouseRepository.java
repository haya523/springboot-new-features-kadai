package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {

    // ===== 固定順（ID昇順）で一覧・検索するためのメソッド =====
    Page<House> findAllByOrderByIdAsc(Pageable pageable);
    Page<House> findByNameLikeOrAddressLikeOrderByIdAsc(String nameLike, String addressLike, Pageable pageable);
    Page<House> findByAddressLikeOrderByIdAsc(String addressLike, Pageable pageable);
    Page<House> findByPriceLessThanEqualOrderByIdAsc(Integer price, Pageable pageable);

    // ===== 互換（他コントローラ／既存コードで参照される可能性があるもの） =====
    // キーワードで名前のみ検索（並びはPageable側のsort設定に従う）
    Page<House> findByNameLike(String nameLike, Pageable pageable);

    // トップページ「新着」用：直近10件（作成日時の降順）
    List<House> findTop10ByOrderByCreatedAtDesc();

    //（必要に応じて使われることがある典型メソッド。定義しておくと互換性が高い）
    Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<House> findAllByOrderByPriceAsc(Pageable pageable);
    Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameLike, String addressLike, Pageable pageable);
    Page<House> findByAddressLikeOrderByCreatedAtDesc(String addressLike, Pageable pageable);
    Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
}
