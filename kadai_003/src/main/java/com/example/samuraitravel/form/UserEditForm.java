package com.example.samuraitravel.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserEditForm {
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String furigana;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String address;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Email
    private String email;
}
