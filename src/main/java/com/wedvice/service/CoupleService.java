package com.wedvice.service;

import com.wedvice.dto.CompleteMatchRequestDto;
import com.wedvice.dto.Gender;
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
                .build());

        userRepository.updateMatchedUserId(user.getId(), partnerUser.getId(), couple.getId());
        userRepository.updateMatchedUserId(partnerUser.getId(), user.getId(), couple.getId());

    }

    @Transactional
    public void completeMatch(Long userId, CompleteMatchRequestDto requestDto) {
        Gender gender = requestDto.getGender();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유효하지 않은 유저의 접근입니다."));
        if (user.getCouple() == null) {
            throw new RuntimeException("커플 매칭이 되지 않았습니다.");
        }
        Couple couple = coupleRepository.findById(user.getCouple().getId()).orElseThrow(() -> new RuntimeException("존재하지 않은 커플입니다."));
        user.setNickname(requestDto.getNickName());
        if (gender.equals(Gender.FEMALE)) {
            if (couple.getBride() != null) {
                throw new IllegalStateException("이미 신부 정보가 등록된 커플입니다.");
            }
            couple.setBride(user);
        } else if (gender.equals(Gender.MALE)) {
            if (couple.getGroom() != null) {
                throw new IllegalStateException("이미 신랑 정보가 등록된 커플입니다.");
            }
            couple.setGroom(user);
        }
    }
}
