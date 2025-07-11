package com.wedvice.user.common;

import com.wedvice.user.entity.User;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTestFixture {

    public static User userWithId(Long id) {
        User user = User.create("oauthId", "kakao");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public static User userWithIdAndName(Long id, String nickname) {
        User user = User.create("oauthId", "kakao");
        user.updateNickname(nickname);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
