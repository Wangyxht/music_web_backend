package com.music.demo.controller;

import com.music.demo.dto.SmsSendDTO;
import com.music.demo.service.SmsService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.utils.RedisUtil;
import com.music.demo.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms/")
@CrossOrigin("*")
public class SmsController {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    SmsUtil smsUtil;

    @Autowired
    SmsService smsService;

    @PostMapping("send")
    ResponseEntity<Object> sendSms(@Validated @RequestBody SmsSendDTO smsSendDTO)
    {
        try {
            if (smsService.sendSms(smsSendDTO)){
                return ResponseEntity
                        .ok()
                        .body(ApiResponse.error("验证码发送成功。"));
            } else return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error("验证码发送失败。"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
