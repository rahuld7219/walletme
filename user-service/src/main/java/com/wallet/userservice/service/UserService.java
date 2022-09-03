package com.wallet.userservice.service;

import com.wallet.userservice.dao.UserRepo;
import com.wallet.userservice.entity.User;
import com.wallet.userservice.request.UserCreationRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    /**
     * create user and returns created user id
     *
     * @param userCreationRequestDTO
     * @return
     */
    public Long createUser(UserCreationRequestDTO userCreationRequestDTO) {
        User user =  User.builder().email(userCreationRequestDTO.getEmail())
                .name(userCreationRequestDTO.getName())
                .phone(userCreationRequestDTO.getPhone())
                .kycId(userCreationRequestDTO.getKycId())
                .build();

        userRepo.save(user); // TODO: handle when this gives error, if user already exist
        return user.getId(); // user refer to the user saved in database, so no need to store the returned user
    }
}
