package com.music.demo.service;

import com.music.demo.dto.SmsSendDTO;
import com.music.demo.utils.RedisUtil;
import com.music.demo.utils.SmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class SmsService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SmsUtil smsUtil;

    public boolean sendSms(SmsSendDTO smsSendDTO) throws Exception
    {
        String phone = smsSendDTO.getPhone();
        String type = smsSendDTO.getType();
        // 验证验证码间隔时间
        // 1min生成间隔
        if (redisUtil.get("sms:limit:" + phone) != null) {
            throw new RuntimeException("验证码重复发送间隔时间为1分钟。");
        }
        // 生成四位随机验证码
        String code = String.valueOf((int) (Math.random() * 9000 + 1000));
        // 将验证码存入Redis
        if (smsUtil.sendSms(phone, type, Map.of("code", code))) {
            redisUtil.set("sms:code:" + phone, code, 60 * 5);
            redisUtil.set("sms:limit:" + phone, "1", 60);
            return true;
        } else return false;

    }

    public boolean verifySms(String phone, String smsCode) throws NoSuchElementException
    {
        String code = (String) redisUtil.get("sms:code:" + phone);

        if (code==null){
            throw new NoSuchElementException("验证手机验证码无效或超时，请重试。");
        }

        return smsCode.equals(code);
    }
}
