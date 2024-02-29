package com.zl.m3u8.main;

import com.zl.m3u8.download.M3u8DownloadFactory;
import com.zl.m3u8.listener.DownloadListener;
import com.zl.m3u8.utils.Constant;

/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:02
 */

public class M3u8Main {

    private static final String M3U8URL = "https://www.gdcr2329.com/apiv2/video/36781/play?sign=ao4nXl9SX2wVc1EWZmMZ7j5K2GkP19yPEJ%2F4QRjT5tYpKCreTN%2BSHC1IFvf%2BaqC3RsBh9UEH63KukhjorZhQy35hkfpvsyMfTARhuRLx%2FaPds53qlWNk0SoFbMeuGRKkULatRC5p%2BiTmrGu9IIoylcInTfseFEP78ZfXY6slb3UR7TGzNpuZ7sZOgU1zVG%2FKeMLHixNPNSP3W9sy9yVbr6hERcnSnsbTRkq8sgOaw9C4Z8nHYD2GDpkiaBbNtt73kddZqLSibA0TZfwNe7fpji9oVpgmoRHZW8WGeN3PHhYuFt5dlk9kLWz7ogRWcNFbucyAaiBBhj9afGZ9Fst%2FolNOksxDD1Eat%2BQVj0EUpnTfZ%2B%2F9DiBHVUpHmwxUHaGFiD9IEMguM%2BzpCxd8DaK7U33iW6NNwNvPoyAdS%2F95RntpW6VpO%2Bb1I4p5DC9vbieGjQ6C1tuAfM%2BupeAhJgh4I4S%2FPLo9UVhCV8ZJoOTqwPZPumhCk3g3o8t6zq814858lHj002ayIcz7A%2F4991gNSTHvDPCIZqoV2gza0TFelequ%2FTNFsw4k%2BwWESW06qgmO0YvgmbC%2FyHmiIoBYFwBrVs%2BRkeFLIhSDlTjKY7iHdI16%2Fd20WhZiCGAMrNxI9TSRwN5JPIMC7%2Fw%2BGkC0cG2dLyYdo%2B3nyLUyvkwuybgQu0g%3D&line=general&movie.m3u8";

    public static void main(String[] args) {

        M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(M3U8URL);
        //设置生成目录
        m3u8Download.setDir("F://m3u8JavaTest");
        //设置视频名称
        m3u8Download.setFileName("test");
        //设置线程数
        m3u8Download.setThreadCount(100);
        //设置重试次数
        m3u8Download.setRetryCount(100);
        //设置连接超时时间（单位：毫秒）
        m3u8Download.setTimeoutMillisecond(10000L);
        /*
        设置日志级别
        可选值：NONE INFO DEBUG ERROR
        */
        m3u8Download.setLogLevel(Constant.INFO);
        //设置监听器间隔（单位：毫秒）
        m3u8Download.setInterval(500L);
        //添加额外请求头
      /*  Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Content-Type", "text/html;charset=utf-8");
        m3u8Download.addRequestHeaderMap(headersMap);*/
        //如果需要的话设置http代理
        //m3u8Download.setProxy("172.50.60.3",8090);
        //添加监听器
        m3u8Download.addListener(new DownloadListener() {
            @Override
            public void start() {
                System.out.println("开始下载！");
            }

            @Override
            public void process(String downloadUrl, int finished, int sum, float percent) {
                System.out.println("下载网址：" + downloadUrl + "\t已下载" + finished + "个\t一共" + sum + "个\t已完成" + percent + "%");
            }

            @Override
            public void speed(String speedPerSecond) {
                System.out.println("下载速度：" + speedPerSecond);
            }

            @Override
            public void end() {
                System.out.println("下载完毕");
            }
        });
        //开始下载
        m3u8Download.start();
    }
}
