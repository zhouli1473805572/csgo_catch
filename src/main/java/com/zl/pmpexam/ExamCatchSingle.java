package com.zl.pmpexam;

import com.alibaba.excel.EasyExcel;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: z.l
 * @date: 2023-02-20
 **/
public class ExamCatchSingle {


    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "F:\\develop\\myweb\\csgo_catch\\src\\main\\resources\\chromedriver.exe");// chromedriver服务地址
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c5e0b8483c_mz3K7kum");

        WebElement element = driver.findElement(By.className("no-login"));
        element.click();

        Thread.sleep(10000);

        driver.get("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6364d80123998_2cxnewMJ");
        Thread.sleep(5000);
        List<PmpExam> pmpExams = new ArrayList<>();

        List<WebElement> elements = driver.findElements(By.className("question-item"));
        for (WebElement question : elements) {
            WebElement type = question.findElement(By.className("question-text"));
            String typeStr = type.getText();

            WebElement headEle = question.findElement(By.className("question-header"));
            String questionStr = headEle.findElement(By.tagName("p")).getText();

            List<WebElement> selects = question.findElements(By.className("question-select-item"));
            List<String> selectStrs = Lists.newArrayList();
            for (WebElement select : selects) {
                WebElement selectStr = select.findElement(By.className("question-select-item__option"));
                WebElement span = selectStr.findElement(By.tagName("span"));
                WebElement p = selectStr.findElement(By.tagName("p"));
                String text = span.getText();
                String text1 = p.getText();
                selectStrs.add(text + text1);
            }

            WebElement correctAnaswerEle = question.findElement(By.className("correct-answer-text"));
            String correctAnaswerStr = correctAnaswerEle.getText();

            String answerAnalysisStr = null;
            try {
                WebElement answerAnalysisEle = question.findElement(By.className("answer-analysis"));
                answerAnalysisStr = answerAnalysisEle.getText();
            } catch (Exception e) {
                e.printStackTrace();
            }


            PmpExam pmpExam = PmpExam.builder().type(typeStr).question(questionStr).select(StringUtils.join(selectStrs, "\r\n")).answer(correctAnaswerStr).analysis(answerAnalysisStr).build();
            pmpExams.add(pmpExam);
            System.out.println(pmpExam);
        }

        addToExcel(pmpExams);
    }

    private static void addToExcel(List<PmpExam> pmpExam) {
        EasyExcel.write(new File("C:\\Users\\admin\\Desktop\\111\\123123.xlsx")).sheet(String.valueOf(0)).sheetNo(0).head(PmpExam.class).doWrite(pmpExam);


    }

}
