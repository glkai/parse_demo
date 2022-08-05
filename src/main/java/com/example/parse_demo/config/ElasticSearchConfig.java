package com.example.parse_demo.config;

/**
 * @author GaoLiuKai
 * @date 2022/8/3 11:56
 */
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * ElasticSearch 配置
 */
@Configuration
public class ElasticSearchConfig {

    /**
     * 协议
     */
    @Value("${elasticsearch.schema:http}")
    private String schema;

    /**
     * 集群地址，如果有多个用“,”隔开
     */
    @Value("${elasticsearch.address}")
    private String address;

    /**
     * 连接超时时间
     */
    @Value("${elasticsearch.connectTimeout:5000}")
    private int connectTimeout;

    /**
     * Socket 连接超时时间
     */
    @Value("${elasticsearch.socketTimeout:10000}")
    private int socketTimeout;

    /**
     * 获取连接的超时时间
     */
    @Value("${elasticsearch.connectionRequestTimeout:5000}")
    private int connectionRequestTimeout;

    /**
     * 最大连接数
     */
    @Value("${elasticsearch.maxConnectNum:100}")
    private int maxConnectNum;

    /**
     * 最大路由连接数
     */
    @Value("${elasticsearch.maxConnectPerRoute:100}")
    private int maxConnectPerRoute;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 拆分地址
        List<HttpHost> hostList = new ArrayList<>();
        String[] hostArray = address.split(",");
        for (String addr : hostArray) {
            String host = addr.split(":")[0];
            String port = addr.split(":")[1];
            hostList.add(new HttpHost(host, Integer.parseInt(port), schema));
        }
        // 转换成 HttpHost 数组
        HttpHost[] httpHost = hostList.toArray(new HttpHost[0]);

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        // 构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHost);
        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
        });
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });

        return new RestHighLevelClient(builder);
    }

}
