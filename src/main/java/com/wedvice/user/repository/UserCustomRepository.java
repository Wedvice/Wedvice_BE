package com.wedvice.user.repository;

import com.wedvice.user.dto.UserDto;
import com.wedvice.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {

    List<UserDto> getAllUserTestExample();

    Optional<User> findByUserWithCoupleAndPartner(Long userId);

    Long findCoupleIdByUserId(Long userId);
}
