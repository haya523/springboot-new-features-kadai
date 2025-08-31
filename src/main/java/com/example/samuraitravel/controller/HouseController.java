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
import com.example.samuraitravel.service.ReviewService;

@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;
    private final ReviewService reviewService;

    public HouseController(HouseRepository houseRepository, ReviewService reviewService) {
        this.houseRepository = houseRepository;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String area,
                        @RequestParam(required = false) Integer price,
                        @RequestParam(required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id",
                                direction = Direction.ASC) Pageable pageable,
                        Model model) {
        // 既存ロジックそのまま（省略）
        return "houses/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Integer id,
                       Model model,
                       @AuthenticationPrincipal UserDetailsImpl login) {

        House house = houseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定の民宿が見つかりません: id=" + id));
        model.addAttribute("house", house);
        model.addAttribute("reservationInputForm", new ReservationInputForm());

        // 最新3件だけ表示
        Page<Review> page = reviewService.getReviewsForHouse(id, 0, 3);
        model.addAttribute("reviews", page.getContent());

        // ★ 総件数（>3 なら「他のレビューも見る」を出す）
        long reviewsTotal = reviewService.countForHouse(id);
        model.addAttribute("reviewsTotal", reviewsTotal);

        // 自分のレビュー（ログイン時のみ）
        if (login != null) {
            reviewService.getUserReviewForHouse(id, login.getUser().getId())
                    .ifPresent(r -> model.addAttribute("myReview", r));
        } else {
            model.addAttribute("myReview", null);
        }

        return "houses/show";
    }
}
