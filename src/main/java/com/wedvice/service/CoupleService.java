package com.wedvice.service;

import com.wedvice.entity.Couple;
import com.wedvice.entity.User;
import com.wedvice.repository.CoupleRepository;
import com.wedvice.repository.UserRepository;
import com.wedvice.util.MatchCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MatchCodeService matchCodeService;
    private final UserRepository userRepository;

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

    @Transactional
    public void matchCouple(long userId, String matchCode) {
        Optional<Long> partnerId = matchCodeService.consumeCode(matchCode);
        long pid = partnerId.orElseThrow(() -> new RuntimeException("만료되었거나 존재하지 않는 매치 코드 입니다. {}".formatted(matchCode)));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유효하지 않은 유저의 접근입니다."));
        User partnerUser = userRepository.findById(pid).orElseThrow(() -> new RuntimeException("존재하지 않는 유저와의 매칭입니다."));

        Couple couple = coupleRepository.save(Couple.builder()
                .bride(user)
                .groom(partnerUser)
                .build());

        userRepository.updateMatchedUserId(userId, pid);
        userRepository.updateMatchedUserId(pid, userId);

    }
}
