package com.ai.medical_diagnosis.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class PythonHttpClient {


    /**
     * 调用 Python 服务，传入图片
     * 主要用于图像分割
     * @param image 图片
     * @param url 接口地址
     * @param type 返回结果类型
     * @return type类型对应的返回结果
     * @param <R> type类型
     */
    public <R> R ImageClient(byte[] image, String url, Class<R> type) {
        String base64Image = Base64.getEncoder().encodeToString(image);
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 使用 JSONObject 构建安全的 JSON 请求体
        JSONObject requestJson = new JSONObject();
        requestJson.put("image", base64Image);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(requestJson.toJSONString(), ContentType.APPLICATION_JSON));
        httpPost.setHeader("Content-Type", "application/json");

        try {
            String responseJson = httpClient.execute(httpPost, response -> {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Python 返回状态码: {}", statusCode);
                    return null;
                }
                return EntityUtils.toString(response.getEntity());
            });

            if (responseJson == null || responseJson.isEmpty()) {
                log.warn("Python 返回空响应");
                return null;
            }

            return JSONObject.parseObject(responseJson, type);

        } catch (Exception e) {
            log.error("调用 Python 服务发生错误: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 调用 Python 服务，传入文本，返回结果
     * 主要用于文本分割
     * @param text 文本
     * @param url 接口地址
     * @param responseType 返回结果类型
     * @return responseType 类型的返回结果
     * @param <R> responseType
     */
    public <R> R textSegmentClient(String text, String url, Class<R> responseType) {
        if (text == null || text.trim().isEmpty()) {
            log.warn("输入文本为空，跳过调用");
            return null;
        }

        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 构建请求 JSON
        JSONObject requestJson = new JSONObject();
        requestJson.put("text", text);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(
                requestJson.toJSONString(),
                ContentType.create("application/json", StandardCharsets.UTF_8)
        ));
        httpPost.setHeader("Content-Type", "application/json");

        try {
            String responseJson = httpClient.execute(httpPost, response -> {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    String errorMsg = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    log.error("Python 返回非200状态码: {}，响应: {}", statusCode, errorMsg);
                    return null;
                }
                return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            });

            if (responseJson == null || responseJson.isEmpty()) {
                log.warn("Python 服务返回空响应");
                return null;
            }

            return JSONObject.parseObject(responseJson, responseType);

        } catch (Exception e) {
            log.error("调用 Python 服务发生异常: {}", e.getMessage(), e);
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (Exception ignored) {
                // 忽略关闭异常
            }
        }
    }
}