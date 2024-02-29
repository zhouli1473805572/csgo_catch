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
public class ExamCatch {


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

        List<String> urls = new ArrayList<>();
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c5e0b8483c_mz3K7kum");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c5e405c42b_QZq9JdyA");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c5e6d19db5_L4TLrIux");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c601c21fe2_i8x3FZ1O");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c6047594a8_moBMxYvs");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_630c60753c427_vsxP3JVp");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6343de0457d82_xXTzysdE");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6343de27d8b50_wlFwPWKP");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_634fca2c06d0b_56zdxMRO");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_635a28375aec2_jF6bBNS6");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6362151490b85_Lzzm0lJw");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6364d80123998_2cxnewMJ");
        urls.add("https://xiaoe-tech.spotoit.com/p/t_pc/pc_evaluation/exam_analysis/ex_6367fb6c0c6b7_C6TOB6xK");

        int index = 0;
        for (String url : urls) {
            driver.get(url);
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

                WebElement answerAnalysisEle = question.findElement(By.className("answer-analysis"));
                String answerAnalysisStr = answerAnalysisEle.getText();

                PmpExam pmpExam = PmpExam.builder().type(typeStr).question(questionStr).select(StringUtils.join(selectStrs, "\r\n")).answer(correctAnaswerStr).analysis(answerAnalysisStr).build();
                pmpExams.add(pmpExam);
                System.out.println(pmpExam);
            }
            addToExcel(pmpExams, index++);
        }


    }

    private static void addToExcel(List<PmpExam> pmpExam, int i) {
        EasyExcel.write(new File("C:\\Users\\admin\\Desktop\\111\\" + i + ".xlsx")).sheet(String.valueOf(i)).sheetNo(i).head(PmpExam.class).
        doWrite(pmpExam);


    }

}
