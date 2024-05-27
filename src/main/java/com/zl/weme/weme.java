package com.zl.weme;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: z.l
 * @date: 2023-05-17
 **/
@Slf4j
public class weme {
    public static void main(String[] args) throws IOException {
        String fileName = "F:\\develop\\myweb\\csgo_catch\\src\\main\\resources\\weme\\xiuren.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true));

        Map<String, Object> temp = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String s1 = null;
        while ((s1 = bufferedReader.readLine()) != null) {
            Map<String, Object> map = JSON.parseObject(s1, Map.class);
            temp.putAll(map);
        }

        System.setProperty("webdriver.chrome.driver", "F:\\develop\\myweb\\csgo_catch\\src\\main\\resources\\chromedriver.exe");// chromedriver服务地址
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("-headless");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--user-agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36'");
        chromeOptions.addArguments("--incognito");
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = new ChromeDriver(chromeOptions);
        for (int i = 1; i < 35; i++) {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get("https://weme.su/mid/xiuren/page/" + i);

            WebElement posts = driver.findElement(By.id("posts"));
            List<WebElement> elements = posts.findElements(By.className("post"));
            String windowHandle;
            for (WebElement element : elements) {
                WebElement outDiv = element.findElement(By.className("img"));
                WebElement imgATag = outDiv.findElement(By.tagName("a"));
                String href = imgATag.getAttribute("href");
                String title = imgATag.getAttribute("title");

                if (temp.containsKey(title)) {
                    continue;
                }
                HashMap<String, List<String>> result = new HashMap<>();
                List<String> urls = new ArrayList<>();
                result.put(title, urls);

                System.out.println(title);
                if (href.contains("19166")) {
                    continue;
                }
                windowHandle = driver.getWindowHandle();
                String js = "window.open('" + href + "')";
                ((JavascriptExecutor) driver).executeScript(js);

                for (String handle : driver.getWindowHandles()) {
                    if (!handle.equals(windowHandle)) {
                        driver.switchTo().window(handle);
                        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                        WebElement content = driver.findElement(By.className("article-content"));
                        List<WebElement> figures = content.findElements(By.className("wp-block-image"));

                        for (WebElement figure : figures) {
                            WebElement imageATage = figure.findElement(By.tagName("a"));
                            String href1 = imageATage.getAttribute("href");
                            urls.add(href1);
                        }
                        driver.close();
                    }
                }
                driver.switchTo().window(windowHandle);
                String s = JSON.toJSONString(result);
                bufferedWriter.write(s);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        }


    }
}
