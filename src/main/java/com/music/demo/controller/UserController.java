package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.*;
import com.music.demo.entity.User;
import com.music.demo.utils.*;
import com.music.demo.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("register")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> register(@Validated @RequestBody UserRegisterDTO userRegisterDTO)
    {

        try{
            String token = userService.register(userRegisterDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(token,"注册成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }

    }

    @PostMapping("login")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
    })
    ResponseEntity<Object> login(@Validated @RequestBody UserLoginDTO userLoginDTO)
    {
        try {
            String token = userService.login(userLoginDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(token,"登录成功"));

        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("sms-login")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> smsLogin(@Validated @RequestBody UserSmsLoginDTO userLoginDTO)
    {
        try {
            String token = userService.smsLogin(userLoginDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(token,"登录成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))
            })
    ResponseEntity<Object> search(@Validated @ModelAttribute UserSearchDTO userSearchDTO)
    {
        try{
            Page<User> userPage = userService.searchUserByUsername(userSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(userPage,"查询成功"));

        } catch (Exception e){

            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }

    }


    @GetMapping("userInf")
    ResponseEntity<Object> getUserInf() {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long id = (Long) map.get("id");
        User user = userService.getById(id);
        return ResponseEntity
                .ok()
                .body(ApiResponse.success(user));
    }

    @PutMapping("retrieve")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> retrieve(@Validated @RequestBody UserRetrieveDTO userRetrieveDTO){
        try{
            userService.retrieve(userRetrieveDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("密码修改成功"));

        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("update")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> update(@RequestBody UserUpdateDTO userUpdateDTO){
        try{
            userService.updateUser(userUpdateDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("用户信息更新成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("update-password")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> updatePassword(@Validated @RequestBody UserPasswordUpdateDTO userPasswordUpdateDTO)
    {
        try{
            userService.updatePassword(
                    userPasswordUpdateDTO.getNewPassword(),
                    userPasswordUpdateDTO.getOldPassword()
            );
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("密码更新成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("user-delete")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> userDelete(@Validated @RequestBody UserDelDTO userDelDTO)
    {
        try{
            userService.userDelete(userDelDTO.getPassword());
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("用户注销成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("admin/delete")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))
            })
    ResponseEntity<Object> adminDelete(@RequestParam Long userId)
    {
        try{
            userService.adminDelete(userId);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("用户注销成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("admin/search")
    ResponseEntity<Object> adminSearch(@Validated @ModelAttribute UserSearchDTO userSearchDTO){
        try{
            Page<User> page =  userService.searchUserAdmin(userSearchDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(page, "用户搜索成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("inf/{userId}")
    ResponseEntity<Object> inf(@NotNull @PathVariable Long userId){
        try{
            User user = userService.getById(userId);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(user, "用户搜索成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
