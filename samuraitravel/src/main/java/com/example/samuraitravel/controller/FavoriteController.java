package com.example.samuraitravel.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.entity.Favorite;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl login,
                        @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable,
                        Model model) {
        if (login == null) return "redirect:/login";
        Page<Favorite> page = favoriteService.getPageForUser(login.getUser().getId(), pageable);
        model.addAttribute("page", page); // ← ★ page に統一
        return "favorites/index";
    }

    @PostMapping("/add")
    public String add(@RequestParam("houseId") Integer houseId,
                      @AuthenticationPrincipal UserDetailsImpl login,
                      HttpServletRequest req) {
        if (login == null) return "redirect:/login";
        favoriteService.add(login.getUser().getId(), houseId);
        String back = req.getHeader("Referer");
        return "redirect:" + (back != null ? back : "/houses/" + houseId);
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("houseId") Integer houseId,
                         @AuthenticationPrincipal UserDetailsImpl login,
                         HttpServletRequest req) {
        if (login == null) return "redirect:/login";
        favoriteService.remove(login.getUser().getId(), houseId);
        String back = req.getHeader("Referer");
        return "redirect:" + (back != null ? back : "/houses/" + houseId);
    }
}
