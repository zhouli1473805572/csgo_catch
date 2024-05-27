package com.zl.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.MapUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author: z.l
 * @date: 2023-05-17
 **/
public class HttpUtils {

    static HashMap<String, String> publicHeader = new HashMap();

    static {
        publicHeader.put("x-forwarded-for", "210.101.131.23");
        publicHeader.put("Referer", "https://gdcr2329.com");
        publicHeader.put("Host", "www.baidu.com");
        publicHeader.put("Connection", "keep-alive");
        publicHeader.put("Agent", "PC");
        publicHeader.put("cookie", "PHPSESSID=3a9aae3b6ec759df64b549f6530d2cd4; Edge-Sticky=NUDmDc9uO/qdKWDm7le8pw==; Hm_lvt_8ec32d81dddd08fb2ff215dd624e5d0f=1684458817; XLA_CI=f3cb6af30aef3f2336cb06440842c029; _ga=GA1.1.1931219709.1684458823; fish_session=6QN09RdOHhQvladnDSWy789i878xvZ3hZO1MqRgF; Hm_lpvt_8ec32d81dddd08fb2ff215dd624e5d0f=1684459188; _ga_TM83R4D3QS=GS1.1.1684458823.1.1.1684459188.0.0.0; AWSALB=ZdNEp+g6vPLbUCqUbxw9kM1e3knyQrgMO/ZNi8Z2ZjGSuQrAwS5WcA3RCN4RIRZbGO3ItZApUETDCkYiVI8pp1o3MRwCT5RnvTQxBYLHjcGJh9XTmM7ORJRgDE+a");
    }

    /**
     * GET---无参
     */
    public static String doGet(String url, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");
        if (headers == null) {
            headers = publicHeader;
        }
        for (String key : headers.keySet()) {
            httpGet.addHeader(key, headers.get(key));
        }


        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMinutes(1))
                .setConnectionRequestTimeout((Timeout.ofMinutes(1)))
                .setRedirectsEnabled(true)
                .build();
        httpGet.setConfig(requestConfig);

        // 响应模型
        CloseableHttpResponse response = null;
        //失败后重试  一直重试5次
        try {
            for (int i = 0; i < 8; i++) {
                try {
                    response = httpClient.execute(httpGet);
                    HttpEntity responseEntity = response.getEntity();
                    if (responseEntity != null) {
                        return EntityUtils.toString(responseEntity);
                    }
                } catch (Exception ioException) {

                    try {
                        int time = new Random().nextInt(5) + 3;
                        System.out.println("第" + (i + 1) + "次请求失败，" + time + "秒后重试" + ioException.getMessage());
                        Thread.sleep(1000 * time);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * GET---有参
     */
    public static void doGetParmas(String url, String params) {
        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // 创建Get请求
        HttpGet httpGet = new HttpGet(url + "?" + params);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMinutes(1))
                .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                .setRedirectsEnabled(true)
                .build();
        httpGet.setConfig(requestConfig);

        // 响应模型
        CloseableHttpResponse response = null;
        try {

            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);

            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();
            System.out.println("响应状态为:" + response.getCode());
            if (responseEntity != null) {
                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * POST---无参测试
     */
    public static CloseableHttpResponse doPost(String url) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustAllStrategy()).build();
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext).build();
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMinutes(1))
                .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                .setRedirectsEnabled(true)
                .build();
        httpPost.setConfig(requestConfig);

        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            System.out.println("响应状态为:" + response.getCode());
            if (responseEntity != null) {
//                System.out.println("响应内容长度为:" + responseEntity.getContentLength());
//                System.out.println("响应内容为:" + EntityUtils.toString(responseEntity));
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * POST---有参测试(对象参数)
     */
    public static String doPostParams(String url, Object params, Map<String, String> headers, Map<String, String> nvps) throws UnsupportedEncodingException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustAllStrategy()).build();
        SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslcontext).build();
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

        // 创建Post请求
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMinutes(1))
                .setConnectionRequestTimeout(Timeout.ofMinutes(1))
                .setRedirectsEnabled(true)
//                .setProxy(new HttpHost("127.0.0.1", 8888))
                .build();
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

        if (headers == null) {
            headers = publicHeader;
        }
        for (String key : headers.keySet()) {
            httpPost.addHeader(key, headers.get(key));
        }

        if (MapUtils.isNotEmpty(nvps)) {
            List<BasicNameValuePair> pairs = new ArrayList<>();
            for (String s : nvps.keySet()) {
                pairs.add(new BasicNameValuePair(s, nvps.get(s)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8));
        }

        if (params != null) {
            String jsonString = JSON.toJSONString(params);
            StringEntity entity = new StringEntity(jsonString, Charset.forName("UTF-8"));
            // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
            httpPost.setHeader("Content-Type", "application/json;charset=utf8");
            httpPost.setEntity(entity);
        }


        // 响应模型
        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Post请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                return EntityUtils.toString(responseEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}