package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /** お気に入り一覧（1ページ10件） */
    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page,
                        Model model,
                        @AuthenticationPrincipal UserDetailsImpl login) {
        if (login == null) {
            return "redirect:/login";
        }
        Page<Favorite> favorites = favoriteService.listMy(login.getUser().getId(), PageRequest.of(page, 10));
        model.addAttribute("favorites", favorites);
        return "favorites/index";
    }

    /** トグル（民宿詳細からのPOST） */
    @PostMapping("/house/{houseId}/toggle")
    public String toggle(@PathVariable Integer houseId,
                         @AuthenticationPrincipal UserDetailsImpl login,
                         RedirectAttributes ra) {
        if (login == null) return "redirect:/login";
        boolean now = favoriteService.toggle(login.getUser().getId(), houseId);
        ra.addFlashAttribute("successMessage", now ? "お気に入りに追加しました。" : "お気に入りを解除しました。");
        return "redirect:/houses/" + houseId;
    }

    /** 一覧からの解除ボタン */
    @PostMapping("/house/{houseId}/remove")
    public String removeFromList(@PathVariable Integer houseId,
                                 @RequestParam(defaultValue = "0") int page,
                                 @AuthenticationPrincipal UserDetailsImpl login,
                                 RedirectAttributes ra) {
        if (login == null) return "redirect:/login";
        favoriteService.toggle(login.getUser().getId(), houseId); // 登録済なら解除される
        ra.addFlashAttribute("successMessage", "お気に入りを解除しました。");
        return "redirect:/favorites?page=" + page;
    }
}
