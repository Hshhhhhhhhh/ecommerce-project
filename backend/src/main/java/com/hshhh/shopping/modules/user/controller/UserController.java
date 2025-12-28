package com.hshhh.shopping.modules.user.controller;

import com.hshhh.shopping.common.Result;
import com.hshhh.shopping.modules.user.dto.AddressDTO;
import com.hshhh.shopping.modules.user.dto.UserUpdateDTO;
import com.hshhh.shopping.modules.user.service.AddressService;
import com.hshhh.shopping.modules.user.service.UserService;
import com.hshhh.shopping.modules.user.vo.AddressVO;
import com.hshhh.shopping.modules.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        String username = getCurrentUsername();
        return Result.success(userService.getUserProfile(username));
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserUpdateDTO userUpdateDTO) {
        String username = getCurrentUsername();
        userService.updateUserProfile(username, userUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "获取收货地址列表")
    @GetMapping("/address")
    public Result<List<AddressVO>> getAddresses() {
        Long userId = getCurrentUserId();
        return Result.success(addressService.getMyAddresses(userId));
    }

    @Operation(summary = "添加收货地址")
    @PostMapping("/address")
    public Result<AddressVO> addAddress(@RequestBody @Valid AddressDTO addressDTO) {
        Long userId = getCurrentUserId();
        return Result.success(addressService.addAddress(userId, addressDTO));
    }

    @Operation(summary = "更新收货地址")
    @PutMapping("/address/{addressId}")
    public Result<Void> updateAddress(@PathVariable Long addressId, @RequestBody @Valid AddressDTO addressDTO) {
        Long userId = getCurrentUserId();
        addressService.updateAddress(userId, addressId, addressDTO);
        return Result.success();
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/address/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        addressService.deleteAddress(userId, addressId);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PutMapping("/address/{addressId}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long addressId) {
        Long userId = getCurrentUserId();
        addressService.setDefaultAddress(userId, addressId);
        return Result.success();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Long getCurrentUserId() {
        return userService.getUserIdByUsername(getCurrentUsername());
    }
}
