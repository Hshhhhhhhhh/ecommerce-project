package com.hshhh.shopping.modules.auth.service.impl;

import com.hshhh.shopping.common.enums.ResultCode;
import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.modules.auth.dto.UserLoginDTO;
import com.hshhh.shopping.modules.auth.dto.UserRegisterDTO;
import com.hshhh.shopping.modules.auth.service.AuthService;
import com.hshhh.shopping.modules.auth.vo.LoginResultVO;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import com.hshhh.shopping.modules.user.vo.UserVO;
import com.hshhh.shopping.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    @Transactional
    public LoginResultVO register(UserRegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(ResultCode.USER_HAS_EXISTED);
        }
        if (registerDTO.getEmail() != null && userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(ResultCode.USER_HAS_EXISTED, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole("USER"); // Default role
        
        userRepository.save(user);

        // Auto login
        String token = jwtProvider.generateToken(user.getUsername(), user.getRole());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setToken(token);
        resultVO.setExpiresIn(expiration / 1000);
        resultVO.setUser(userVO);

        return resultVO;
    }

    @Override
    public LoginResultVO login(UserLoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        // loginDTO.getUsername() could be username or email
        User user = userRepository.findByUsernameOrEmail(loginDTO.getUsername(), loginDTO.getUsername())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));

        String token = jwtProvider.generateToken(user.getUsername(), user.getRole());

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        LoginResultVO resultVO = new LoginResultVO();
        resultVO.setToken(token);
        resultVO.setExpiresIn(expiration / 1000); // Convert ms to seconds
        resultVO.setUser(userVO);

        return resultVO;
    }
}
