package com.music.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.music.demo.dto.*;
import com.music.demo.entity.User;

public interface UserService extends IService<User> {

    User getUserByPhone(String phone);

    User getUserByUsername(String username);

    String login(UserLoginDTO userLoginDTO);

    String smsLogin(UserSmsLoginDTO userLoginDTO);

    String register(UserRegisterDTO userRegisterDTO);

    void retrieve(UserRetrieveDTO userRetrieveDTO);

    Page<User> searchUserByUsername(UserSearchDTO userSearchDTO);

    void userDelete(String password);

    void adminDelete(Long userId);

    void updatePassword(String newPassword, String oldPassword);

    void updateUser(UserUpdateDTO userUpdateDTO);

    Page<User> searchUserAdmin(UserSearchDTO userSearchDTO);
}
