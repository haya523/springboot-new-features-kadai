package com.example.samuraitravel.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, length=50)
    private String name;

    @Column(nullable=false, length=50)
    private String furigana;

    @Column(name="postal_code", nullable=false, length=50)
    private String postalCode;

    @Column(nullable=false, length=255)
    private String address;

    @Column(name="phone_number", nullable=false, length=50)
    private String phoneNumber;

    @Column(nullable=false, unique=true, length=255)
    private String email;

    @Column(nullable=false, length=255)
    private String password;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable=false)
    private Boolean enabled;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;

    // ※ レビュー双方向関連は後で必要になったら解放してください
    // @OneToMany(mappedBy = "user")
    // private List<Review> reviews = new ArrayList<>();
}
