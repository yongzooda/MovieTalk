package com.sec.movietalk.userinfo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDto {

    private String nickname;
    private String password;
    private String confirmPassword;
}
