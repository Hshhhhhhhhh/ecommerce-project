package com.hshhh.shopping.modules.auth.service;

import com.hshhh.shopping.modules.auth.dto.UserLoginDTO;
import com.hshhh.shopping.modules.auth.dto.UserRegisterDTO;
import com.hshhh.shopping.modules.auth.vo.LoginResultVO;

public interface AuthService {
    LoginResultVO register(UserRegisterDTO registerDTO);
    LoginResultVO login(UserLoginDTO loginDTO);
}
