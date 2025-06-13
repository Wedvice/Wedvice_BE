package com.wedvice.couple.service;

import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.Gender;
import com.wedvice.couple.dto.UserDto;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.NoTowPeopleException;
import com.wedvice.couple.exception.NotInputStatusException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MatchCodeService matchCodeService;
    private final UserRepository userRepository;


    @Transactional
    public void matchCouple(long userId, String matchCode) {
        Optional<Long> partnerId = matchCodeService.getCodeUserId(matchCode);
        long pid = partnerId.orElseThrow(() -> new RuntimeException("만료되었거나 존재하지 않는 매치 코드 입니다. %s".formatted(matchCode)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 유저의 접근입니다."));
        User partnerUser = userRepository.findById(pid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저와의 매칭입니다."));

        if (userId == partnerUser.getId()) throw new RuntimeException("본인과는 커플이 될 수 없습니다.");

        Couple couple = coupleRepository.save(Couple.builder().build());

        user.setCouple(couple);
        partnerUser.setCouple(couple);

        // 모든 로직이 정상적으로 완료된 후 코드 소모
        matchCodeService.removeCode(matchCode);
    }

    @Transactional
    public void completeMatch(Long userId, CompleteMatchRequestDto requestDto) {
        Gender gender = requestDto.getGender();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유효하지 않은 유저의 접근입니다."));
        if (user.getCouple() == null) {
            throw new RuntimeException("커플 매칭이 되지 않았습니다.");
        }
        if (user.getRole() != null) {
            throw new RuntimeException("이미 닉네임 설정 및 신랑 신부 역할이 입력되었습니다.");
        }

        user.setNickname(requestDto.getNickName());
        if (gender.equals(Gender.BRIDE)) {
            user.setRole(User.Role.BRIDE);
        } else if (gender.equals(Gender.GROOM)) {
            user.setRole(User.Role.GROOM);
        }
    }

    //    코드 입력(couple외래키가 있냐 없냐) -> 닉네임(nickname) -> 성별(role) ->
    //    입력 다 하면 상대방 입력 대기 뻉글뻉글 -> 홈 화면에 언제 보내줄꺼냐?
    //    (user.couple.users(!=id).getnickname,role !=null
    public CoupleHomeInfoResponseDto getCoupleInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 유저의 접근입니다."));

        Couple couple = user.getCouple();
        if (couple == null) {
            throw new RuntimeException("아직 커플이 성립되지 않았습니다."); // 매치코드 입력 단계
        }

        if (user.getNickname() == null || user.getRole() == null) {
            throw new NotInputStatusException(); // 닉네임 or 성별 미입력
        }

        List<User> users = couple.getUsers();
        if (users == null || users.size() != 2) {
            throw new NoTowPeopleException();
        }

        User partner = users.stream()
                .filter(u -> !u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("상대방 정보가 존재하지 않습니다."));

        if (partner.getNickname() == null || partner.getRole() == null) {
            throw new RuntimeException("아직 상대방의 매칭 정보가 완료되지 않았습니다.");
        }

        // 신랑/신부 구분
        User groom = user.getRole() == User.Role.GROOM ? user : partner;
        User bride = user.getRole() == User.Role.BRIDE ? user : partner;

        return CoupleHomeInfoResponseDto.builder()
                .groomDto(UserDto.of(groom, userId))
                .brideDto(UserDto.of(bride, userId))
                .weddingDate(couple.getWeddingDate())
                .build();
    }
}
