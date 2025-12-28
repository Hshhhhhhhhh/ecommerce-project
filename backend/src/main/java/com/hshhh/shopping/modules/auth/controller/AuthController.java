package com.hshhh.shopping.modules.auth.controller;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.auth.dto.UserLoginDTO;
import com.hshhh.shopping.modules.auth.dto.UserRegisterDTO;
import com.hshhh.shopping.modules.auth.service.AuthService;
import com.hshhh.shopping.modules.auth.vo.LoginResultVO;
import com.hshhh.shopping.modules.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<LoginResultVO> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
        return Result.success(authService.register(registerDTO));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResultVO> login(@RequestBody @Valid UserLoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }
}
