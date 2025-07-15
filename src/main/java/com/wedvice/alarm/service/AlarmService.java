package com.wedvice.alarm.service;

import com.wedvice.alarm.dto.AlarmResponseDto;
import com.wedvice.alarm.repository.AlarmRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public List<AlarmResponseDto> getMyAlarms(Long userId) {
        // TODO: 실제 로직 구현 예정
        return Collections.emptyList();
    }

    public void readAlarm(Long alarmId, Long userId) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
