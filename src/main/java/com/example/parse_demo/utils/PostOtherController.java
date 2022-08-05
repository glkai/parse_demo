package com.example.parse_demo.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 14:14
 */
public class PostOtherController {


    public static JSONObject post(String url, HashMap<String, Object> textMap) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse httpResponse = null;
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(200000).setSocketTimeout(200000000)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        ContentType contentType = ContentType.create("multipart/form-data", StandardCharsets.UTF_8);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);

        for (String str : textMap.keySet()) {
            try {
                System.out.println(str+"--->"+textMap.get(str).toString());
                multipartEntityBuilder.addTextBody(str, textMap.get(str).toString(), contentType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity responseEntity = httpResponse.getEntity();

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String resultStr = null;
        JSONObject result = null;
        if (statusCode == 200) {
            BufferedReader reader = null;
            InputStreamReader isr = null;
            try {
                isr = new InputStreamReader(responseEntity.getContent(), StandardCharsets.UTF_8);
                reader = new BufferedReader(isr);
                StringBuilder buffer = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);

                }
                resultStr = buffer.toString();
                JSONObject jsonObject = JSONObject.parseObject(resultStr);
                String data = jsonObject.get("data").toString();
                result = JSONObject.parseObject(data.substring(1, data.length() - 1));
            } catch (UnsupportedOperationException | IOException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (isr != null) {
                        isr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            httpClient.close();
            httpResponse.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
