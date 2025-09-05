package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseRepository houseRepository;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    /**
     * 民宿一覧（固定順：ID昇順）
     */
    @GetMapping
    public String index(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "area", required = false) String area,
            @RequestParam(name = "price", required = false) Integer price,
            // 画面からページングされるので size 指定のみ。sort は固定順(ID ASC)にする
            @PageableDefault(size = 9, sort = "id", direction = Direction.ASC) Pageable pageable,
            Model model) {

        Page<House> housePage;

        // キーワード検索（民宿名 or 住所）
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            housePage = houseRepository.findByNameLikeOrAddressLikeOrderByIdAsc(like, like, pageable);

        // エリア（住所に含まれる都道府県など）
        } else if (area != null && !area.isBlank()) {
            String like = "%" + area.trim() + "%";
            housePage = houseRepository.findByAddressLikeOrderByIdAsc(like, pageable);

        // 予算（以下）
        } else if (price != null) {
            housePage = houseRepository.findByPriceLessThanEqualOrderByIdAsc(price, pageable);

        // 条件なし：全件（固定順）
        } else {
            housePage = houseRepository.findAllByOrderByIdAsc(pageable);
        }

        model.addAttribute("housePage", housePage); // ★ 一覧は housePage に統一
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price);

        return "houses/index";
    }

    /**
     * 民宿詳細
     */
    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer id,
                       @AuthenticationPrincipal UserDetailsImpl login,
                       Model model,
                       @PageableDefault(size = 6, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

        // 民宿本体
        House house = houseRepository.findById(id).orElseThrow();
        model.addAttribute("house", house); // ★ 正しく house を渡す

        // レビュー（ページング）
        Page<Review> reviews = reviewService.findPageForHouse(id, pageable);
        model.addAttribute("reviews", reviews);

        long reviewsTotal = reviewService.countForHouse(id);
        model.addAttribute("reviewsTotal", reviewsTotal);

        // 自分のレビュー（ログイン時のみ）
        if (login != null) {
            reviewService.getUserReviewForHouse(id, login.getUser().getId())
                         .ifPresent(r -> model.addAttribute("myReview", r));
        } else {
            model.addAttribute("myReview", null);
        }

        // お気に入り状態（ログイン時のみ）
        boolean isFavorite = false;
        if (login != null) {
            isFavorite = favoriteService.isFavorite(login.getUser().getId(), id);
        }
        model.addAttribute("isFavorite", isFavorite);

        return "houses/show";
    }
}
