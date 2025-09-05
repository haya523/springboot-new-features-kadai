package com.example.samuraitravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // ✅ マイページ（閲覧）
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl loginUser, Model model) {
        User user = userRepository.getReferenceById(loginUser.getUser().getId());
        model.addAttribute("user", user);
        return "user/index";
    }

    // ✅ 編集フォーム
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl loginUser, Model model) {
        User user = userRepository.getReferenceById(loginUser.getUser().getId());

        // 空のフォームを作って、値を1つずつセット
        UserEditForm form = new UserEditForm();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setFurigana(user.getFurigana());
        form.setPostalCode(user.getPostalCode());
        form.setAddress(user.getAddress());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setEmail(user.getEmail());

        model.addAttribute("userEditForm", form);
        return "user/edit";
    }


    // ✅ 更新処理
    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetailsImpl loginUser,
                         @ModelAttribute @Validated UserEditForm userEditForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        // 他人のIDを更新しようとした場合 → 強制的にログインユーザーのIDに置き換える
        userEditForm.setId(loginUser.getUser().getId());

        // メール重複チェック
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        // 更新
        userService.update(userEditForm);

        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        return "redirect:/user";
    }
}
