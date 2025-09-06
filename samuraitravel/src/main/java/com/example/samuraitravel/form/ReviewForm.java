package com.example.samuraitravel.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReviewForm {
    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    private String comment;

    // --- 後方互換（旧コードが getScore()/setScore() を呼ぶ場合のため） ---
    public Integer getScore() { return rating; }
    public void setScore(Integer score) { this.rating = score; }
}
