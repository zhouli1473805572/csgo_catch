package com.zl.mjsq;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: z.l
 * @date: 2022-10-14
 **/
public class VideoM3u8Catch {
    static Map<String, String> cache = new HashMap<>();

    public static void main(String[] args) throws Exception {
        File resultFile = new File("F:\\develop\\myweb\\csgo_catch\\src\\main\\resources\\result.txt");
        readToCache(resultFile);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resultFile, true));

        System.setProperty("webdriver.chrome.driver", "F:\\develop\\myweb\\csgo_catch\\src\\main\\resources\\chromedriver.exe");// chromedriver服务地址
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = new ChromeDriver(chromeOptions);

//        Dimension dimension = new Dimension(1920, 1080);
//        driver.manage().window().setSize(dimension);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        String url = "https://www.mjsq8.net/index.php/vod/show/by/time/id/1/page/1.html";
        driver.get(url);
        while (true) {
            List<WebElement> videoBoxs = driver.findElements(By.className("videoBox"));
            String win = driver.getWindowHandle();

            for (WebElement videoBox : videoBoxs) {
                String title = videoBox.findElement(By.className("title")).getText();
                if (cache.containsKey(title)) {
                    continue;
                }

                boolean flag = openVideoPage(driver, win, videoBox);
                if (!flag) {
                    continue;
                }
                try {
                    WebElement videoAction = driver.findElement(By.className("video-action"));

                    String js = "return player_aaaa;";
                    Object o = ((JavascriptExecutor) driver).executeScript(js);
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSON(o).toString());
                    String m3u8Url = jsonObject.getString("url");
                    String result = title + "@@##@@" + m3u8Url;
                    if (!cache.containsKey(result)) {
                        cache.put(title, m3u8Url);
                        bufferedWriter.write(result);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        System.out.println(m3u8Url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //失败再重试一次
                    driver.close();
                    driver.switchTo().window(win);

                    boolean flag2 = openVideoPage(driver, win, videoBox);
                    if (!flag2) {
                        continue;
                    }
                }

                driver.close();
                driver.switchTo().window(win);
            }
            driver.findElement(By.className("layui-laypage-next")).click();
            Thread.sleep(1000);
        }
    }

    private static void readToCache(File resultFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(resultFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] split = line.split("@@##@@");
            cache.put(split[0], split[1]);
        }
        bufferedReader.close();
    }

    private static boolean openVideoPage(WebDriver driver, String win, WebElement videoBox) {
        //进去单个列表页
        String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
        videoBox.sendKeys(selectLinkOpeninNewTab);

        //切换到播放按钮页面
        Iterator<String> iterator = driver.getWindowHandles().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            driver.switchTo().window(next);
        }

        WebElement aListDiv = driver.findElement(By.className("detail-play-list"));
        List<WebElement> alist = aListDiv.findElements(By.tagName("a"));
        WebElement aTag = alist.get(0);
        if (aTag.getText().trim().contains("在線播放")) {
            aTag.click();
            return true;
        } else {
            //返回列表页
            driver.close();
            driver.switchTo().window(win);
            return false;
        }
    }

}
