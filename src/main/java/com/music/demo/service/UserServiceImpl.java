package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.*;
import com.music.demo.entity.User;
import com.music.demo.mapper.UserMapper;
import com.music.demo.mapstruct.UserDtoMapper;
import com.music.demo.utils.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TextAuditUtil textAuditUtil;

    private String generateUserToken(@NotNull User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("id", user.getId());
        claims.put("type", user.getType().getValue());
        return JwtUtil.generateToken(claims);
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, phone);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<User> searchUserByUsername(UserSearchDTO userSearchDTO) {
        Page<User> userPageRequest = new Page<>(userSearchDTO.getPage(), userSearchDTO.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().like(User::getUsername, userSearchDTO.getUsername());
        // 获取必要信息
        queryWrapper.select("id", "username", "introduction");
        // 查询分页
        userMapper.selectPage(userPageRequest, queryWrapper);
        return userPageRequest;
    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws IllegalArgumentException{
        // 查询登录用户（根据用户名）
        User user = getUserByUsername(userLoginDTO.getUsername());
        if(user == null){
            throw new IllegalArgumentException("登录失败，用户名不存在");
        }
        String password = MD5Util.generateMD5(userLoginDTO.getPassword());
        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("登录失败，密码错误。");
        }

        return generateUserToken(user);
    }

    @Override
    public String smsLogin(UserSmsLoginDTO userLoginDTO) throws IllegalArgumentException{
        // 查询登录用户（根据手机）
        User user = getUserByPhone(userLoginDTO.getPhone());
        if(user == null){
            // 删除Redis
            redisUtil.delete("sms:code:" + userLoginDTO.getPhone());
            throw new IllegalArgumentException("登录失败，手机号未注册");
        }
        // 检查用户状态（如账号是否被锁定或已删除）
        if (Boolean.TRUE.equals(user.getIsDelete())) {
            throw new IllegalArgumentException("账号已被删除，请联系管理员。");
        }
        // 验证验证码
        if (!smsService.verifySms(userLoginDTO.getPhone(), userLoginDTO.getSmsCode())) {
            throw new IllegalArgumentException("验证码错误。");
        }

        // 删除Redis
        redisUtil.delete("sms:code:" + user.getPhone());
        return generateUserToken(user);
    }

    @Override
    public void retrieve(UserRetrieveDTO userRetrieveDTO) throws RuntimeException {
        // 验证验证码
        if (!smsService.verifySms(userRetrieveDTO.getPhone(), userRetrieveDTO.getSmsCode())) {
            throw new IllegalArgumentException("验证码错误。");
        }

        User user = getUserByPhone(userRetrieveDTO.getPhone());

        // 查询用户名/手机是否被注册
        if (user == null) {
            // 删除Redis
            redisUtil.delete("sms:code:" + userRetrieveDTO.getPhone());
            throw new IllegalArgumentException("手机号未注册。");
        }

        // 检查用户状态（如账号是否被锁定或已删除）
        if (Boolean.TRUE.equals(user.getIsDelete())) {
            throw new IllegalArgumentException("账号已被删除，请联系管理员。");
        }
        // 更新用户密码（使用 MD5 加密）
        String encryptedPassword = MD5Util.generateMD5(userRetrieveDTO.getPassword());
        user.setPassword(encryptedPassword);
        if(!updateById(user)){
            throw new RuntimeException("更新失败，请重试");
        }

        redisUtil.delete("sms:code:" + user.getPhone());
    }

    @Override
    public String register(UserRegisterDTO userRegisterDTO){
        // 验证验证码
        if (!smsService.verifySms(userRegisterDTO.getPhone(), userRegisterDTO.getSmsCode())) {
            throw new IllegalArgumentException("验证码错误。");
        }
        // 查询用户名/手机是否被注册
        if (getUserByPhone(userRegisterDTO.getPhone()) != null) {
            // 删除Redis
            redisUtil.delete("sms:code:" + userRegisterDTO.getPhone());
            throw new IllegalArgumentException("手机号被注册。");
        }
        if(getUserByUsername(userRegisterDTO.getUsername()) != null) {
            throw new IllegalArgumentException("用户名被注册。");
        }

        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                userRegisterDTO.getUsername(),
                "nickname_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        // 转换为实体
        User user = UserDtoMapper.INSTANCE.toEntity(userRegisterDTO);
        user.setIsDelete(false);
        user.setType(userRegisterDTO.getType());
        user.setRegisterTime(LocalDateTime.now());
        user.setPassword(MD5Util.generateMD5(userRegisterDTO.getPassword()));
        // 注册
        if (userMapper.insert(user) == 0){
            throw new RuntimeException("注册失败，请重试");
        }
        // 删除Redis
        redisUtil.delete("sms:code:" + user.getPhone());
        // 返回token
        User userRegistered = getUserByUsername(user.getUsername());
        return generateUserToken(userRegistered);
    }

    /**
     * 用户注销
     * @param password 密码
     */
    @Override
    public void userDelete(String password){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);

        User user = userMapper.selectOne(queryWrapper);
        if(user == null || !user.getPassword().equals(MD5Util.generateMD5(password))){
            throw new IllegalArgumentException("注销失败。");
        }

        remove(queryWrapper);
    }

    /**
     * 管理员删除用户
     * @param userId 用户ID
     */
    @Override
    public void adminDelete(Long userId){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");

        if(!type.equals(0)){
            throw new IllegalArgumentException("无管理员权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);

        remove(queryWrapper);
    }

    /**
     * 更改密码
     * @param newPassword 新密码
     * @param oldPassword 旧密码
     */
    @Override
    public void updatePassword(String newPassword, String oldPassword){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", userId);

        User user = userMapper.selectOne(queryWrapper);
        if(user == null || !user.getPassword().equals(MD5Util.generateMD5(oldPassword))){
            throw new IllegalArgumentException("密码验证失败。");
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("password", MD5Util.generateMD5(newPassword));
        updateWrapper.eq("id", userId);

        userMapper.update(user, updateWrapper);
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO){
        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                userUpdateDTO.getUsername(),
                "nickname_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        Long userId =  userUpdateDTO.getId();
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();

        if(userId == null){
            userId = (Long) map.get("id");
            userUpdateDTO.setId(userId);

        } else {
            Integer type = (Integer) map.get("type");

            if(!type.equals(0)){
                throw new IllegalArgumentException("无管理员权限");
            }
        }

        User user = UserDtoMapper.INSTANCE.toEntity(userUpdateDTO);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        userMapper.update(user, updateWrapper);
    }

    @Override
    public Page<User> searchUserAdmin(UserSearchDTO userSearchDTO){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");

        if(!type.equals(0)){
            throw new IllegalArgumentException("无管理员权限");
        }

        Page<User> page = new Page<>(userSearchDTO.getPage(), userSearchDTO.getLimit());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", userSearchDTO.getUsername());

        return userMapper.selectPage(page, queryWrapper);
    }
}
