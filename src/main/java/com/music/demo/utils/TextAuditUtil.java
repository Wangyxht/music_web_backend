package com.music.demo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.green20220302.Client;
import com.aliyun.green20220302.models.TextModerationRequest;
import com.aliyun.green20220302.models.TextModerationResponse;
import com.aliyun.green20220302.models.TextModerationResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TextAuditUtil {

    @Value("${aliyun.green.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.green.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.green.region-id:cn-shanghai}")
    private String regionId;

    @Value("${aliyun.green.endpoint:green-cip.cn-shanghai.aliyuncs.com}")
    private String endpoint;

    @Value("${aliyun.green.read-timeout:6000}")
    private int readTimeout;

    @Value("${aliyun.green.connect-timeout:3000}")
    private int connectTimeout;

    /**
     * 创建阿里云内容审核 Client 实例
     */
    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setRegionId(regionId)
                .setEndpoint(endpoint)
                .setReadTimeout(readTimeout)
                .setConnectTimeout(connectTimeout);
        return new Client(config);
    }

    /**
     * 文本审核方法
     *
     * @param content 待审核文本
     * @param serviceCode 阿里云内容安全服务的 serviceCode
     * @return 审核结果 JSON 格式
     */
    public Map<String, String> auditText(String content, String serviceCode) {
        if (content == null || content.trim().isEmpty()) {
            log.warn("待审核的文本内容为空。");
            return null;
        }

        try {
            Client client = createClient();
            RuntimeOptions runtime = new RuntimeOptions();
            runtime.readTimeout = 10000;
            runtime.connectTimeout = 10000;

            JSONObject serviceParameters = new JSONObject();
            serviceParameters.put("content", content);

            TextModerationRequest request = new TextModerationRequest()
                    .setService(serviceCode)
                    .setServiceParameters(serviceParameters.toJSONString());

            TextModerationResponse response = client.textModerationWithOptions(request, runtime);
            String jsonString =  handleResponse(response);
            // 解析外层 JSON 对象
            JSONObject jsonObject = JSON.parseObject(jsonString);
            Map<String, String> resultMap = new HashMap<>();
            // 获取 labels 字段
            String labels = jsonObject.getString("labels");
            resultMap.put("labels", labels != null ? labels : "");

            // 获取 reason 字段并解析为 HashMap
            String reasonStr = jsonObject.getString("reason");
            if (reasonStr != null && !reasonStr.trim().isEmpty()) {
                Map<String, String> reasonMap = JSON.parseObject(reasonStr, HashMap.class);

                // 获取 riskLevel, riskTips, riskWords
                resultMap.put("riskLevel", reasonMap.getOrDefault("riskLevel", ""));
                resultMap.put("riskTips", reasonMap.getOrDefault("riskTips", ""));
                resultMap.put("riskWords", reasonMap.getOrDefault("riskWords", ""));
            } else {
                resultMap.put("riskLevel", null);
                resultMap.put("riskTips", null);
                resultMap.put("riskWords", null);
            }

            return resultMap;
        } catch (TeaException e) {
            log.error("文本审核时出现 TeaException，错误信息：{}", e.getMessage());
        } catch (Exception e) {
            log.error("文本审核时出现异常，错误信息：{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 处理文本审核响应
     *
     * @param response 文本审核响应
     * @return 审核结果 JSON 格式
     */
    private String handleResponse(TextModerationResponse response) {
        if (response == null) {
            log.error("审核响应为空。");
            return "Text moderation response is null.";
        }

        if (response.getStatusCode() == 200) {
            TextModerationResponseBody result = response.getBody();
            if (result != null && result.getCode() == 200) {
                log.info("文本审核成功：{}", JSON.toJSONString(result.getData()));
                return JSON.toJSONString(result.getData());
            } else {
                log.warn("文本审核失败，错误码：{}，错误信息：{}", result.getCode(), result.getMessage());
            }
        } else {
            log.error("审核响应状态码异常：{}", response.getStatusCode());
        }
        return "Text moderation not successful.";
    }
}