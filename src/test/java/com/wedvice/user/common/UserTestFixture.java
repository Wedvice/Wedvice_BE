package com.wedvice.user.common;

import com.wedvice.user.entity.User;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.test.util.ReflectionTestUtils;

public class UserTestFixture {

    private static final AtomicLong oauthCounter = new AtomicLong(1);

    public static User createUserWithUniqueOauthId() {
        String uniqueOauthId = "oauth-test-" + oauthCounter.getAndIncrement();
        return User.create(uniqueOauthId, "kakao");
    }


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

    public static User createWithRoleAndNickName(User.Role role, String nickName) {
        User user = createUserWithUniqueOauthId();
        user.updateRole(role);
        user.updateNickname(nickName);
        return user;
    }
}
