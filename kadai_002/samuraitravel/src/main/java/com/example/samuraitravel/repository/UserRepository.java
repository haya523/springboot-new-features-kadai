package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;   // ★ 追加
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    // ★ 認証で使う。User.role を一緒にロードして LazyInitialization を回避
    @EntityGraph(attributePaths = "role")
    User findByEmail(String email);

    // 既存の検索（そのままでOK）
    Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
}
