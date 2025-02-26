package com.wedvice.service;

import com.wedvice.entity.Couple;
import com.wedvice.entity.User;
import com.wedvice.repository.CoupleRepository;
import com.wedvice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    @Transactional
    public User saveOrGetUser(String oauthId, String provider, String nickname, String profileImageUrl) {
        return userRepository.findByOauthIdAndProvider(oauthId, provider)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .oauthId(oauthId)
                            .provider(provider)
                            .nickname(nickname)
                            .profileImageUrl(profileImageUrl)
                            .build();
                    return userRepository.save(newUser);
                });
    }

    @Transactional
    public void updateUserInfo(Long userId, String nickname, Long matchedUserId, String memo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (nickname != null) user.setNickname(nickname);
        if (matchedUserId != null) user.setMatchedUserId(matchedUserId);
        if (memo != null) user.setMemo(memo);

        userRepository.save(user);
    }

    @Transactional
    public void updateMemo(Long userId, String memo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setMemo(memo);
        userRepository.save(user);
    }

    @Transactional
    public void updateMatchedUserAndCreateCouple(Long userId, Long matchedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        User matchedUser = userRepository.findById(matchedUserId)
                .orElseThrow(() -> new RuntimeException("매칭된 사용자를 찾을 수 없습니다."));

        // ✅ User의 matchedUserId 양방향 업데이트
        user.setMatchedUserId(matchedUserId);
        matchedUser.setMatchedUserId(userId);
        userRepository.save(user);
        userRepository.save(matchedUser);

        // ✅ 이미 존재하는 커플인지 확인

        boolean coupleExists = coupleRepository.existsByGroomAndBride(user, matchedUser) ||
                coupleRepository.existsByGroomAndBride(matchedUser, user);

        if (!coupleExists) {
            // ✅ Couple 테이블에 새로운 로우 생성
            Couple couple = Couple.builder()
                    .groom(user)
                    .bride(matchedUser)
                    .build();
            coupleRepository.save(couple);
        }
    }

}


