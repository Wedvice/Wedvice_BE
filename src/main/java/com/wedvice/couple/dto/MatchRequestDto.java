package com.wedvice.couple.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchRequestDto {

    @Size(min = 5, max = 30 , message = "매치코드 size 벗어남.")
    private String matchCode;
}
