package com.wedvice.user.repository;

import com.wedvice.user.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserConfigRepository extends JpaRepository<UserConfig, Long> {
    Optional<UserConfig> findByUser_Id(Long userId);
}
