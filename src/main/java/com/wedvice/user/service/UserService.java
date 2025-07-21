package com.wedvice.user.service;

import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.security.login.JwtTokenProvider;
import com.wedvice.security.login.RedirectEnum;
import com.wedvice.security.login.RedirectResponseDto;
import com.wedvice.user.dto.MemoRequestDto;
import com.wedvice.user.dto.MyAccountResponseDto;
import com.wedvice.user.dto.MyPageMainResponseDto;
import com.wedvice.user.dto.PartnerImageAndColorResponseDto;
import com.wedvice.user.dto.UpdateColorConfigRequestDto;
import com.wedvice.user.dto.UpdateNicknameRequestDto;
import com.wedvice.user.dto.UserColorConfigResponseDto;
import com.wedvice.user.dto.UserDto;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.UserConfig;
import com.wedvice.user.exception.TokenInvalidException;
import com.wedvice.user.exception.TokenMismatchException;
import com.wedvice.user.exception.TokenNotFoundException;
import com.wedvice.user.exception.UnknownTokenException;
import com.wedvice.user.repository.UserConfigRepository;
import com.wedvice.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CoupleRepository coupleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserConfigRepository userConfigRepository;

    @Transactional
    public User saveOrGetUser(String oauthId, String provider, String profileImageUrl) {
        return userRepository.findByOauthId(oauthId)
            .orElseGet(() -> {
                User newUser = User.create(oauthId, provider);
                newUser.updateProfileImage(profileImageUrl);
                return userRepository.save(newUser);
            });
    }


    @Transactional
    public Map<String, Object> updateRefresh(Cookie cookie) {
        if (cookie == null) {
            throw new TokenNotFoundException();
        }

        String refreshToken = cookie.getValue();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenInvalidException();
        }

        try {
            jwtTokenProvider.validateToken(refreshToken);

            String userId = jwtTokenProvider.getUserId(refreshToken);
            User user = userRepository.findById(Long.parseLong(userId)).orElseThrow();

            String savedRefreshToken = user.getRefreshToken();
            if (!savedRefreshToken.equals(refreshToken)) {
                throw new TokenMismatchException();
            }

            String newAccessToken = jwtTokenProvider.generateAccessToken(
                String.valueOf(user.getId()), user.getNickname(),
                String.valueOf(user.getOauthId()));
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(
                String.valueOf(user.getId()), user.getNickname(),
                String.valueOf(user.getOauthId()));

            user.updateRefreshToken(newRefreshToken);

            return createTokenResult(newAccessToken, newRefreshToken);
        } catch (RuntimeException e) {
            if (e instanceof TokenMismatchException || e instanceof TokenNotFoundException
                || e instanceof TokenInvalidException) {
                throw e;
            }
            e.printStackTrace();
            throw new UnknownTokenException();
        }
    }

    private Map<String, Object> createTokenResult(String accessToken, String refreshToken) {
        Map<String, Object> result = new HashMap<>();

        HttpHeaders headers = createTokenHeader(refreshToken);
        headers.add("Authorization", "Bearer " + accessToken); // 🔥 accessToken 헤더에 추가

        result.put("headers", headers);

        return result;
    }

    private HttpHeaders createTokenHeader(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie",
            createTokenCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7));
        return headers;
    }

    private HttpHeaders deleteTokenHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", createTokenCookie("refreshToken", "", 0));
        return headers;
    }

    private String createTokenCookie(String name, String token, int maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, token)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .maxAge(maxAge)
            .build();
        return cookie.toString();
    }


    @Transactional(readOnly = true)
    public UserDto getUserInfo(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return UserDto.builder()
            .id(user.getId())
            .memo(user.getMemo())
            .nickname(user.getNickname())
            .profileImageUrl(user.getProfileImageUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public List<UserDto> getAllUserTestExample() {
        return userRepository.getAllUserTestExample();
    }

    @Transactional(readOnly = true)
    public RedirectResponseDto getRedirectStatus(Long userId) {
        // 반복되는 구문, UserReader 컴포넌트 고려
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        return RedirectResponseDto.from(determineMatchingFlow(user));
    }

    private RedirectEnum determineMatchingFlow(User user) {
        if (!user.isMatched()) {
            return RedirectEnum.JUST_USER;
        }

        if (!user.isInfoCompleted()) {
            return RedirectEnum.NOT_COMPLETED;
        }

        if (!user.isPartnerInfoCompleted()) {
            return RedirectEnum.ONLY_COMPLETED;
        }

        return RedirectEnum.PAIR_COMPLETED;
    }

    @Transactional
    public void updateMemo(Long userId, MemoRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        user.updateMemo(requestDto.getMemo());
    }

    @Transactional(readOnly = true)
    public Long getCoupleIdForUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        if (user.getCouple() == null) {
            throw new InvalidUserAccessException();
        }
        return user.getCouple().getId();
    }

    @Transactional(readOnly = true)
    public MyPageMainResponseDto getMyPageInfo(Long userId) {
        User user = userRepository.findUserWithCoupleAndConfigById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        User partner = user.getPartnerOrThrow();

        // MyPageMainResponseDto의 정적 팩토리 메서드를 사용하여 DTO 생성
        return MyPageMainResponseDto.of(user, partner, user.getUserConfig());
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        user.updateProfileImage(null);
    }

    @Transactional
    public void changeProfileImage(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        user.updateProfileImage("updated");
    }

    @Transactional(readOnly = true)
    public PartnerImageAndColorResponseDto getPartnerImageAndColor(Long userId) {
        User user = userRepository.findUserWithCoupleAndConfigById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        User partner = user.getPartnerOrThrow();

        return PartnerImageAndColorResponseDto.of(partner, user.getUserConfig());
    }

    @Transactional
    public void deletePartnerConnection(Long userId) {
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        if (!user.isMatched()) {
            throw new NotMatchedYetException();
        }

        Couple couple = user.getCouple();

        // 양쪽 사용자의 couple 참조를 null로 설정 (JPA가 update 쿼리 생성)
        couple.getUsers().forEach(u -> u.matchCouple(null));

        // Couple 엔티티 삭제
        coupleRepository.delete(couple);
    }

    @Transactional(readOnly = true)
    public MyAccountResponseDto getMyAccountInfo(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        return MyAccountResponseDto.of(user);
    }

    @Transactional
    public void updateNickname(Long userId, UpdateNicknameRequestDto requestDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        user.updateNickname(requestDto.getNewNickname());
    }

    @Transactional
    public void deleteMyAccount(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);

        // 사용자가 커플인 경우, 커플 관계 해체 (Couple 엔티티 삭제)
        if (user.isMatched()) {
            Couple couple = user.getCouple();
            // 양쪽 사용자의 couple 참조를 null로 설정 (JPA가 update 쿼리 생성)
            couple.getUsers().forEach(u -> u.matchCouple(null));
            coupleRepository.delete(couple);
        }

        // 사용자 엔티티 삭제 (UserConfig, UserAlarmConfig는 cascade에 의해 함께 삭제됨)
        userRepository.delete(user);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        user.updateRefreshToken(null);
    }

    @Transactional(readOnly = true)
    public UserColorConfigResponseDto getUserColorConfig(Long userId) {
        UserConfig userConfig = userConfigRepository.findByUser_Id(userId)
            .orElseThrow(InvalidUserAccessException::new); // UserConfig가 없으면 예외 처리
        return UserColorConfigResponseDto.of(userConfig);
    }

    @Transactional
    public void updateColorConfig(Long userId, UpdateColorConfigRequestDto requestDto) {
        UserConfig userConfig = userConfigRepository.findByUser_Id(userId)
            .orElseThrow(InvalidUserAccessException::new);

        if (requestDto.getMyColor() != null) {
            userConfig.updateMyColor(requestDto.getMyColor());
        }
        if (requestDto.getPartnerColor() != null) {
            userConfig.updatePartnerColor(requestDto.getPartnerColor());
        }
    }
}


