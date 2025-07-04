package com.wedvice.user.repository;

import com.wedvice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , UserCustomRepository {

    Optional<User> findByOauthId(String oauthId);

}

