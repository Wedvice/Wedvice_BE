package com.wedvice.couple.service;

import com.wedvice.common.exception.CustomException;
import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.Gender;
import com.wedvice.couple.dto.UserDto;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.*;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.coupletask.service.CoupleTaskService;
import com.wedvice.user.entity.User;
import com.wedvice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    private final CoupleTaskService coupleTaskService;


    @Transactional
    public void matchCouple(long userId, String matchCode) {
        Optional<Long> partnerId = matchCodeService.getCodeUserId(matchCode);
        long pid = partnerId.orElseThrow(() -> new MatchCodeExpiredException(matchCode));

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidUserAccessException::new);
        User partnerUser = userRepository.findById(pid)
                .orElseThrow(UserNotFoundException::new);

        if (userId == partnerUser.getId()) throw new SamePersonMatchException();

        Couple couple = coupleRepository.save(Couple.builder().build());

        user.matchCouple(couple);
        partnerUser.matchCouple(couple);

//     커플 생성될 때 기본 task ,subtask 매핑

        if (!coupleTaskService.isCoupleHavingTasks(couple)) {

            coupleTaskService.createCoupleWithTasks(couple);
        }


        // 모든 로직이 정상적으로 완료된 후 코드 소모
        matchCodeService.removeCode(matchCode);
    }

    @Transactional
    public void completeMatch(Long userId, CompleteMatchRequestDto requestDto) {
        Gender gender = requestDto.getGender();
        User user = userRepository.findById(userId).orElseThrow(InvalidUserAccessException::new);
        if (user.getCouple() == null) {
            throw new NotMatchedYetException();
        }
        if (user.getRole() != null) {
            throw new AlreadyMatchedException();
        }

        Optional<User> first = user.getCouple().getUsers().stream().filter(u -> !u.getId().equals(user.getId())).findFirst();

        User partner = first.orElseThrow();

        User.Role role = gender.equals(Gender.BRIDE) ? User.Role.BRIDE : User.Role.GROOM;

        if(partner.getRole() .equals(role)) {
            throw new SameRoleException();
        }



        user.changeNickname(requestDto.getNickName());
        user.changeRole(role);


    }

    //    코드 입력(couple외래키가 있냐 없냐) -> 닉네임(nickname) -> 성별(role) ->
    //    입력 다 하면 상대방 입력 대기 뻉글뻉글 -> 홈 화면에 언제 보내줄꺼냐?
    //    (user.couple.users(!=id).getnickname,role !=null
    public CoupleHomeInfoResponseDto getCoupleInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(InvalidUserAccessException::new);

        Couple couple = user.getCouple();
        if (couple == null) {
            throw new RuntimeException("아직 커플이 성립되지 않았습니다."); // 매치코드 입력 단계
        }

        if (user.getNickname() == null || user.getRole() == null) {
            throw new NotMatchedYetException(); // 닉네임 or 성별 미입력
        }

        List<User> users = couple.getUsers();
        if (users == null || users.size() != 2) {
            throw new NoTowPeopleException();
        }

        User partner = users.stream()
                .filter(u -> !u.getId().equals(userId))
                .findFirst()
                .orElseThrow(PartnerNotFoundException::new);

        if (partner.getNickname() == null || partner.getRole() == null) {
            throw new PartnerIncompleteException();
        }

        // 신랑/신부 구분
        User groom = user.getRole() == User.Role.GROOM ? user : partner;
        User bride = user.getRole() == User.Role.BRIDE ? user : partner;

//        isPartner true -> 상대방 , false -> 자기자신
        return CoupleHomeInfoResponseDto.builder()
                .groomDto(UserDto.of(groom, userId))
                .brideDto(UserDto.of(bride, userId))
                .weddingDate(couple.getWeddingDate())
                .build();
    }
}
