package com.wedvice.couple.repository;

import com.wedvice.couple.entity.Couple;

public interface CoupleCustomRepository {

    Couple findCoupleByUserId(Long userId);

}
