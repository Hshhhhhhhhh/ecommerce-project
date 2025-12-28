package com.hshhh.shopping.modules.user.service;

import com.hshhh.shopping.modules.user.dto.AddressDTO;
import com.hshhh.shopping.modules.user.vo.AddressVO;

import java.util.List;

public interface AddressService {
    List<AddressVO> getMyAddresses(Long userId);
    AddressVO addAddress(Long userId, AddressDTO addressDTO);
    void updateAddress(Long userId, Long addressId, AddressDTO addressDTO);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
}
