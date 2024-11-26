package com.music.demo.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SmsUtil {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.region-id}")
    private String regionId;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.register-template-code}")
    private String registerTemplateCode;

    @Value("${aliyun.sms.login-template-code}")
    private String loginTemplateCode;

    /**
     * 创建阿里云短信 Client 实例
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("dysmsapi.aliyuncs.com");
        return new Client(config);
    }

    /**
     * 发送短信
     *
     * @param phoneNumber 接收短信的手机号
     * @param templateParam 短信模板参数（JSON 格式）
     * @return 发送结果
     */
    public boolean sendSms(String phoneNumber, String templateType, Map<String, String> templateParam) {
        try {
            Client client = createClient();
            String templateParamJson = mapToJson(templateParam);

            SendSmsRequest request = new SendSmsRequest()
                    .setSignName(signName)
                    .setTemplateCode(templateType.equals("register") ? registerTemplateCode : loginTemplateCode)
                    .setPhoneNumbers(phoneNumber)
                    .setTemplateParam(templateParamJson);

            SendSmsResponse response = client.sendSms(request);
            SendSmsResponseBody responseBody = response.getBody();

            if ("OK".equals(responseBody.getCode())) {
                log.info("短信发送成功，手机号：{}，消息：{}", phoneNumber, responseBody.getMessage());
                return true;
            } else {
                log.error("短信发送失败，错误码：{}，错误信息：{}", responseBody.getCode(), responseBody.getMessage());
                return false;
            }
        } catch (TeaException e) {
            log.error("发送短信时出现 TeaException，错误信息：{}", e.getMessage());
        } catch (Exception e) {
            log.error("发送短信时出现异常，错误信息：{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 将 Map 转换为 JSON 字符串
     *
     * @param param Map 参数
     * @return JSON 字符串
     */
    private String mapToJson(Map<String, String> param) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, String> entry : param.entrySet()) {
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // 移除最后一个逗号
        }
        json.append("}");
        return json.toString();
    }
}
