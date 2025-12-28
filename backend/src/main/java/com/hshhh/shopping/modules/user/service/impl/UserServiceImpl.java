package com.hshhh.shopping.modules.user.service.impl;

import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.common.enums.ResultCode;
import com.hshhh.shopping.modules.user.dto.UserUpdateDTO;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import com.hshhh.shopping.modules.user.service.UserService;
import com.hshhh.shopping.modules.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserVO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        return convertToVO(user);
    }

    @Override
    public UserVO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        updateUser(user, userUpdateDTO);
    }

    @Override
    @Transactional
    public void updateUserProfile(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        updateUser(user, userUpdateDTO);
    }

    @Override
    public Long getUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));
        return user.getId();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    private void updateUser(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        userRepository.save(user);
    }
}
