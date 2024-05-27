package com.zl.ins_image;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zl.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: z.l
 * @date: 2023-05-17
 **/
@Slf4j
public class InsCatch {

    public static void main(String[] args) {
        HashMap<String, String> headers = new HashMap();
        headers.put("x-forwarded-for", "210.101.131.23");
        headers.put("Referer", "https://6u5hfe.xyz");
        headers.put("Authorization", "BearereyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJodXNpd2FuYSIsImxvZ2luX3R5cGUiOiIzIiwiaXNzIjoiaCIsImlhdCI6MTY4NDI5MTU3MiwianRpIjoiNTk1MDQ2NzIifQ.ouuLZQAmv7wQ_prAoU1_N1kbQNN6TUQJv-HtZznj1gMB6KSewCbhVqiVU2Qq6vAYbp-kyO20TwjAo2nyp6VvBQ");

        List<String> fileList;
        File file = new File("D:\\ins\\");
        fileList = Arrays.stream(file.listFiles()).map(File::getName).collect(Collectors.toList());

        String tagResponse = HttpUtils.doGet("https://ins620.com/api/post/app/tag/private/list?tagType=0", headers);
        if (tagResponse != null) {
            JSON.parseObject(tagResponse).getJSONArray("data").forEach(tag -> {
                        JSONObject tagJsonObject = (JSONObject) tag;
                        String tagId = tagJsonObject.getString("tagId");
                        System.out.println("tagId= " + tagId);
                        getImageList(headers, tagId, fileList);
                    }
            );
        }
        getImageList(headers, "0", fileList);
    }

    private static void getImageList(HashMap<String, String> headers, String tagId, List<String> fileList) {
        String response = HttpUtils.doGet("https://ins620.com/api/inst/app/recommend/private/post/hot/" + tagId + "?page=1&size=600&sort=asc&postType=0", headers);
        if (response != null) {
            JSON.parseObject(response).getJSONObject("data").getJSONArray("resultList").forEach(item -> {

                JSONObject jsonObject = (JSONObject) item;
                String title = jsonObject.getString("content").trim()
                        .replace("?", "")
                        .replace(":", "")
                        .replace(".", "");
                if (StringUtils.isBlank(title)) {
                    return;
                }

                if (fileList.contains(title)) {
                    return;
                } else {
                    fileList.add(title);
                }

                File file = new File("D:\\ins\\" + title);
                if (!file.exists()) {
                    file.mkdirs();
                }

                System.out.println(title);
                jsonObject.getJSONArray("fileList").forEach(fileJson -> {
                    try {
                        JSONObject fileObject = (JSONObject) fileJson;
                        String url = fileObject.getString("url");
                        if (StringUtils.isBlank(url)) {
                            url = fileObject.getString("blurredUrl");
                        }
                        String fileName = fileObject.getString("fileId") + ".jpg";
                        System.out.println(fileName + " " + url);
                        byte[] decode = null;
                        try {
                            String imageBase64 = HttpUtils.doGet(url + ".txt", headers);
                            imageBase64 = imageBase64.substring(imageBase64.indexOf(",") + 1);
                            decode = Base64.getDecoder().decode(imageBase64);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("解析失败，再次尝试");
                            Thread.sleep(1000 * 5);
                            String imageBase64 = HttpUtils.doGet(url + ".txt", headers);
                            imageBase64 = imageBase64.substring(imageBase64.indexOf(",") + 1);
                            decode = Base64.getDecoder().decode(imageBase64);
                        }

                        FileUtils.writeByteArrayToFile(new File(file, fileName), decode);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

        }
    }
}
