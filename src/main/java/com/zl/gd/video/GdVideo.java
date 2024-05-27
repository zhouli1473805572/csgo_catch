package com.zl.gd.video;

import com.alibaba.fastjson2.JSON;
import com.zl.gd.model.*;
import com.zl.utils.HttpUtils;
import com.zl.utils.ImageUtil;
import com.zl.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: z.l
 * @date: 2023-05-24
 **/
public class GdVideo {
    public static void main(String[] args) throws InterruptedException, IOException {
        HashMap<String, String> headers = new HashMap();
        headers.put("x-forwarded-for", "210.101.131.23");
        headers.put("Referer", "https://gdcr2329.com");
        headers.put("Host", "www.gdcr2329.com");
        headers.put("Connection", "keep-alive");
        headers.put("Agent", "PC");
        headers.put("cookie", "PHPSESSID=3a9aae3b6ec759df64b549f6530d2cd4; Edge-Sticky=NUDmDc9uO/qdKWDm7le8pw==; Hm_lvt_8ec32d81dddd08fb2ff215dd624e5d0f=1684458817; XLA_CI=f3cb6af30aef3f2336cb06440842c029; _ga=GA1.1.1931219709.1684458823; fish_session=6QN09RdOHhQvladnDSWy789i878xvZ3hZO1MqRgF; Hm_lpvt_8ec32d81dddd08fb2ff215dd624e5d0f=1684459188; _ga_TM83R4D3QS=GS1.1.1684458823.1.1.1684459188.0.0.0; AWSALB=ZdNEp+g6vPLbUCqUbxw9kM1e3knyQrgMO/ZNi8Z2ZjGSuQrAwS5WcA3RCN4RIRZbGO3ItZApUETDCkYiVI8pp1o3MRwCT5RnvTQxBYLHjcGJh9XTmM7ORJRgDE+a");


        List<String> fileList = new ArrayList<>();
        File file = new File("D:\\gdvideo\\");
        if (!file.exists()) {
            file.mkdirs();
        } else {
            fileList = Arrays.stream(file.listFiles()).map(File::getName).collect(Collectors.toList());
        }
        int page;
        System.out.println("正在爬取总页数");

        List<String> urls = new ArrayList<>();
        urls.add("https://www.gdcr3353.com/apiv2/video/search?is_av=1&sort=latest&page=");
        urls.add("https://www.gdcr3353.com/apiv2/video/search?is_av=0&sort=latest&page=1");
        for (String url : urls) {
            String totalResponse = HttpUtils.doGet(url + "1&num=20", headers);
            if (totalResponse != null) {
                Response response = JSON.parseObject(totalResponse, Response.class);
                Integer total = response.getData().getTotal();
                if (total % 20 == 0) {
                    page = total / 20;
                } else {
                    page = total / 20 + 1;
                }
                System.out.println("总页数为：" + page);

                for (int i = 1; i < page + 1; i++) {
                    System.out.println("正在爬取第" + i + "页");
                    headers.put("x-forwarded-for", IpUtils.getRandomIp());
                    headers.put("X-Real-IP", IpUtils.getRandomIp());
                    String videoListResponse = HttpUtils.doGet(url + i + "&num=20", headers);
                    Response videoListJson = JSON.parseObject(videoListResponse, Response.class);
                    DataModel dataModel = videoListJson.getData();
                    List<ImageOrVideoModel> imageOrVideoModels = dataModel.getData();
                    for (ImageOrVideoModel imageOrVideoModel : imageOrVideoModels) {
                        String title = imageOrVideoModel.getMovieName().trim()
                                .replace("?", "")
                                .replace(":", "")
                                .replace(".", "");
                        System.out.println(title);
                        if (fileList.contains(title)) {
                            System.out.println("已存在：" + title);
                            continue;
                        } else {
                            fileList.add(title);
                        }

                        String filePath = "D:\\gdvideo\\" + title;
                        new File(filePath).mkdirs();
                        ImageUtil.downloadImg(imageOrVideoModel.getMovieCover(), filePath, "封面");

                        int movieId = imageOrVideoModel.getMovieId();
                        String movieResJson = HttpUtils.doGet("https://www.gdcr2329.com/apiv2/video/" + movieId, headers);
                        ImageOrVideoResponse imageListRes = JSON.parseObject(movieResJson, ImageOrVideoResponse.class);
                        ImageOrVideoModel data = imageListRes.getData();
                        List<MovieUrl> movieM3u8Url = data.getMovieM3u8Url();
                        for (MovieUrl movieUrl : movieM3u8Url) {
                            if (StringUtils.isNotBlank(movieUrl.getUrl())) {
                                String m3u8rl = "https://www.gdcr2329.com" + movieUrl.getUrl();

//                                FileUtils.write(new File(filePath + File.separator + "url.m3u8"), , "UTF-8");
                            }
                        }
                    }
                }
            }
        }


    }
}
