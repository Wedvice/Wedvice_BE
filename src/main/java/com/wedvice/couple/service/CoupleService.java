package com.wedvice.couple.service;

import com.wedvice.couple.dto.CompleteMatchRequestDto;
import com.wedvice.couple.dto.CoupleHomeInfoResponseDto;
import com.wedvice.couple.dto.Gender;
import com.wedvice.couple.dto.UserDto;
import com.wedvice.couple.entity.Couple;
import com.wedvice.couple.exception.AlreadyMatchedException;
import com.wedvice.couple.exception.InvalidUserAccessException;
import com.wedvice.couple.exception.MatchCodeExpiredException;
import com.wedvice.couple.exception.NotMatchedYetException;
import com.wedvice.couple.exception.PartnerIncompleteException;
import com.wedvice.couple.exception.PartnerMustEnterMatchCode;
import com.wedvice.couple.exception.SamePersonMatchException;
import com.wedvice.couple.exception.SameRoleException;
import com.wedvice.couple.exception.UserNotFoundException;
import com.wedvice.couple.repository.CoupleRepository;
import com.wedvice.couple.util.MatchCodeService;
import com.wedvice.task.service.TaskService;
import com.wedvice.user.entity.User;
import com.wedvice.user.entity.User.Role;
import com.wedvice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoupleService {

    private final CoupleRepository coupleRepository;
    private final MatchCodeService matchCodeService;
    private final UserRepository userRepository;
    private final TaskService taskService;


    @Transactional
    public void matchCouple(long userId, String matchCode) {
        long pid = matchCodeService
            .getCodeUserId(matchCode)
            .orElseThrow(() -> new MatchCodeExpiredException(matchCode));

        User user = userRepository.findById(userId)
            .orElseThrow(InvalidUserAccessException::new);
        User partnerUser = userRepository.findById(pid)
            .orElseThrow(UserNotFoundException::new);

        Couple couple = coupleRepository.save(Couple.create());

        if (user.equals(partnerUser)) {
            throw new SamePersonMatchException();
        }
//            커플중심으로 유저의 연관관계 맺는것도 괜찮은거같음.
        user.matchCouple(couple);
        partnerUser.matchCouple(couple);

        //커플 생성될 때 기본 task ,subtask 매핑
        //Cascade로 Task/SubTask도 함께 저장

        couple.initializeTasks(taskService.findAllTask());

        // 모든 로직이 정상적으로 완료된 후 코드 소모
        matchCodeService.removeCode(matchCode);
    }

    @Transactional
    public void completeMatch(Long userId, CompleteMatchRequestDto requestDto) {
        Gender gender = requestDto.getGender();
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        if (user.getRole() == Role.BRIDE || user.getRole() == Role.GROOM) {
            throw new AlreadyMatchedException();
        }

        User partner = user.getPartnerOrThrow();

        User.Role role = gender.equals(Gender.BRIDE) ? User.Role.BRIDE : User.Role.GROOM;
        if (partner.getRole().equals(role)) {
            throw new SameRoleException();
        }

        user.updateNickname(requestDto.getNickName());
        user.updateRole(role);
    }


    public CoupleHomeInfoResponseDto getCoupleInfo(Long userId) {
        User user = userRepository.findByUserWithCoupleAndPartner(userId)
            .orElseThrow(InvalidUserAccessException::new);

        Couple couple = user.getCouple();
        if (!user.isMatched()) {
            throw new PartnerMustEnterMatchCode();
        }

        if (!user.isInfoCompleted()) {
            throw new NotMatchedYetException();
        }

        if (!user.isPartnerInfoCompleted()) {
            throw new PartnerIncompleteException();
        }

        User partner = user.getPartnerOrThrow();

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
