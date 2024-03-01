package com.zl.weme;

import com.alibaba.fastjson.JSON;
import com.zl.utils.ImageUtil;

import java.io.*;
import java.util.*;

/**
 * @author: z.l
 * @date: 2024-02-27
 **/
public class DownLoadImages {
    public static void main(String[] args) throws IOException {
        File imageParentFile = new File("/volume1/java/weme/image/");
        Set<String> hasDownload = new HashSet<>();
        for (File file : imageParentFile.listFiles()) {
            System.out.println("asd" + file.getName());
            for (File listFile : file.listFiles()) {
                hasDownload.add(listFile.getName());
            }
        }

        File txtParentFile = new File("/volume1/java/weme/txt/");

        System.out.println(txtParentFile.getName());
        for (File txtFile : txtParentFile.listFiles()) {
            System.out.println(txtFile.getName());
            Map<String, List<String>> temp = new LinkedHashMap();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(txtFile));
            String s1;
            while ((s1 = bufferedReader.readLine()) != null) {
                Map<String, List<String>> map = JSON.parseObject(s1, LinkedHashMap.class);
                temp.putAll(map);
            }

            String fileName = txtFile.getName().replace(".txt", "");

            for (Map.Entry<String, List<String>> entry : temp.entrySet()) {
                String key = entry.getKey();

                if (hasDownload.contains(key)) {
                    continue;
                }
                String pathname = "/volume1/java/weme/image/" + fileName + "/" + key;
                System.out.println(pathname);
                File file = new File(pathname);
                if (!file.exists()) {
                    file.mkdirs();
                }
                List<String> value = entry.getValue();
                int i = 1;
                for (String url : value) {
                    ImageUtil.downloadImg(url, pathname, String.valueOf(i++));
                }
            }
        }


    }
}
