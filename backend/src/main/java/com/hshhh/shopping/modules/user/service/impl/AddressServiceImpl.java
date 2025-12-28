package com.hshhh.shopping.modules.user.service.impl;

import com.hshhh.shopping.common.enums.ResultCode;
import com.hshhh.shopping.exception.BusinessException;
import com.hshhh.shopping.modules.user.dto.AddressDTO;
import com.hshhh.shopping.modules.user.entity.Address;
import com.hshhh.shopping.modules.user.entity.User;
import com.hshhh.shopping.modules.user.repository.AddressRepository;
import com.hshhh.shopping.modules.user.repository.UserRepository;
import com.hshhh.shopping.modules.user.service.AddressService;
import com.hshhh.shopping.modules.user.vo.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public List<AddressVO> getMyAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressVO addAddress(Long userId, AddressDTO addressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_EXIST));

        Address address = new Address();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUser(user);

        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            clearDefaultAddress(userId);
        }

        addressRepository.save(address);
        return convertToVO(address);
    }

    @Override
    @Transactional
    public void updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        Address address = getAddressCheckOwner(userId, addressId);
        
        BeanUtils.copyProperties(addressDTO, address);
        
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            clearDefaultAddress(userId);
            address.setIsDefault(true);
        }
        
        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressCheckOwner(userId, addressId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        Address address = getAddressCheckOwner(userId, addressId);
        clearDefaultAddress(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private Address getAddressCheckOwner(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return address;
    }

    private void clearDefaultAddress(Long userId) {
        List<Address> defaultAddresses = addressRepository.findByUserIdAndIsDefaultTrue(userId);
        for (Address addr : defaultAddresses) {
            addr.setIsDefault(false);
            addressRepository.save(addr);
        }
    }

    private AddressVO convertToVO(Address address) {
        AddressVO vo = new AddressVO();
        BeanUtils.copyProperties(address, vo);
        return vo;
    }
}
