package com.wedvice.service;

import com.wedvice.entity.Couple;
import com.wedvice.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;

    @Transactional
    public void updateWeddingDate(Long coupleId, String weddingDate) {
        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(() -> new RuntimeException("커플 정보를 찾을 수 없습니다."));

        couple.setWeddingDate(LocalDate.parse(weddingDate));
        coupleRepository.save(couple);
    }

    @Transactional(readOnly = true)
    public Couple getCouple(Long coupleId) {
        return coupleRepository.findById(coupleId)
                .orElseThrow(() -> new RuntimeException("커플 정보를 찾을 수 없습니다."));
    }
}
