package com.hshhh.shopping.modules.user.service;

import com.hshhh.shopping.modules.user.dto.UserUpdateDTO;
import com.hshhh.shopping.modules.user.vo.UserVO;

public interface UserService {

    /**
     * 获取用户个人信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserProfile(Long userId);

    /**
     * 获取用户个人信息
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getUserProfile(String username);

    /**
     * 更新用户个人信息
     * @param userId 用户ID
     * @param userUpdateDTO 更新信息
     */
    void updateUserProfile(Long userId, UserUpdateDTO userUpdateDTO);

    /**
     * 更新用户个人信息
     * @param username 用户名
     * @param userUpdateDTO 更新信息
     */
    void updateUserProfile(String username, UserUpdateDTO userUpdateDTO);

    /**
     * 根据用户名获取用户ID
     * @param username 用户名
     * @return 用户ID
     */
    Long getUserIdByUsername(String username);

    long countUsers();
}
