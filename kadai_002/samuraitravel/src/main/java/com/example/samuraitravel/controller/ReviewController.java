package com.example.samuraitravel.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewForm;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // 民宿ごとのレビュー一覧
    @GetMapping("/house/{houseId}")
    public String list(@PathVariable Integer houseId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        Page<Review> reviews = reviewService.getReviewsForHouse(houseId, page, size);
        model.addAttribute("reviews", reviews);
        model.addAttribute("houseId", houseId);
        return "reviews/index";
    }

    // 新規投稿フォーム（要ログイン）
    @GetMapping("/house/{houseId}/new")
    public String newForm(@PathVariable Integer houseId,
                          @AuthenticationPrincipal UserDetailsImpl principal,
                          Model model) {
        Integer userId = principal.getUser().getId();

        return reviewService.getUserReviewForHouse(houseId, userId)
                .map(r -> "redirect:/reviews/" + r.getId() + "/edit")
                .orElseGet(() -> {
                    model.addAttribute("houseId", houseId);
                    model.addAttribute("reviewForm", new ReviewForm());
                    return "reviews/new";
                });
    }

    // 作成（要ログイン）  ※クラスに /reviews が付くのでパスは /house/{houseId}
    @PostMapping("/house/{houseId}")
    public String create(@PathVariable Integer houseId,
                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                         @Validated @ModelAttribute ReviewForm form,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("houseId", houseId);
            return "reviews/new";
        }
        reviewService.create(houseId, userDetails.getUser().getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
        return "redirect:/houses/" + houseId;
    }

    // 編集フォーム（要ログイン）
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id,
                       @AuthenticationPrincipal UserDetailsImpl principal,
                       Model model) {
        Review r = reviewService.getOwn(id, principal.getUser().getId());
        ReviewForm f = new ReviewForm();
        f.setRating(r.getRating());
        f.setComment(r.getComment());

        model.addAttribute("reviewId", id);
        model.addAttribute("reviewForm", f);
        return "reviews/edit";
    }

    // 更新（要ログイン）
    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute ReviewForm reviewForm,
                         BindingResult br,
                         @AuthenticationPrincipal UserDetailsImpl principal,
                         Model model,
                         RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("reviewId", id);
            return "reviews/edit";
        }
        Review updated = reviewService.update(id, principal.getUser().getId(), reviewForm);
        ra.addFlashAttribute("successMessage", "レビューを更新しました。");
        return "redirect:/reviews/house/" + updated.getHouse().getId();
    }

    // 削除（要ログイン）
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id,
                         @AuthenticationPrincipal UserDetailsImpl principal,
                         RedirectAttributes ra) {
        Integer houseId = reviewService.delete(id, principal.getUser().getId());
        ra.addFlashAttribute("successMessage", "レビューを削除しました。");
        return "redirect:/reviews/house/" + houseId;
    }
    @GetMapping("/reviews/house/{houseId}")
    public String listByHouse(@PathVariable Integer houseId,
                              @RequestParam(defaultValue = "0") int page,
                              Model model,
                              @AuthenticationPrincipal UserDetailsImpl login) {

        Page<Review> reviews = reviewService.getReviewsForHouse(houseId, page, 10);
        model.addAttribute("reviews", reviews);
        model.addAttribute("houseId", houseId);

        Integer myReviewId = null;
        if (login != null) {
            myReviewId = reviewService.findMyReview(houseId, login.getUser().getId())
                                      .map(Review::getId)
                                      .orElse(null);
        }
        model.addAttribute("myReviewId", myReviewId);

        return "reviews/index"; // 実際のテンプレート名に合わせて
    }


}
