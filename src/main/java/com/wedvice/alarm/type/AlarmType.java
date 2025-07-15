package com.wedvice.alarm.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlarmType {

    NEW_LIST("%s님이 새로운 리스트를 등록했어요."),

    TASK_COMPLETED("%s가 모두 완료되었어요."),

    COMMENT_ADDED("새로운 댓글이 달렸어요: \"%s\""),

    TODAY_TASK("%s개의 리스트가 예정되어 있어요.");

    private final String template;

    
}
