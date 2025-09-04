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
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {

    private final HouseRepository houseRepository;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    public HouseController(HouseRepository houseRepository,
                           ReviewService reviewService,
                           FavoriteService favoriteService) {
        this.houseRepository = houseRepository;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
    }

    // 民宿一覧（既存のまま。中身はあなたの実装に合わせてOK）
    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String area,
                        @RequestParam(required = false) Integer price,
                        @RequestParam(required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) {
        // ここは既存の実装を使ってください（検索や並び替えなど）
        // model.addAttribute(...) を必要に応じて設定
        return "houses/index";
    }

    // 民宿詳細
    @GetMapping("/{id}")
    public String show(@PathVariable Integer id,
                       Model model,
                       @AuthenticationPrincipal UserDetailsImpl login) {

        House house = houseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定の民宿が見つかりません: id=" + id));

        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());

        // 最新3件レビュー（あなたのサービス実装に合わせて）
        Page<Review> page = reviewService.getReviewsForHouse(id, 0, 3);
        model.addAttribute("reviews", page.getContent());

        long reviewsTotal = reviewService.countForHouse(id);
        model.addAttribute("reviewsTotal", reviewsTotal);

        // 自分のレビュー（ログイン時のみ）
        if (login != null) {
            reviewService.getUserReviewForHouse(id, login.getUser().getId())
                    .ifPresent(r -> model.addAttribute("myReview", r));

            // ★ お気に入り状態（ログイン時のみ判定）
            boolean favorited = favoriteService.isFavorited(login.getUser().getId(), id);
            model.addAttribute("favorited", favorited);
        } else {
            model.addAttribute("myReview", null);
            model.addAttribute("favorited", false);
        }

        return "houses/show";
    }
}
