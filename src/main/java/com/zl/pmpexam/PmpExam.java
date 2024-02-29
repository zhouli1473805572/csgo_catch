package com.zl.pmpexam;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: z.l
 * @date: 2023-02-20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmpExam {
    @ExcelProperty(value = "题目类型")
    private String type;
    @ExcelProperty(value = "题目")
    private String question;
    @ExcelProperty(value = "选项")
    private String select;
    @ExcelProperty(value = "答案")
    private String answer;
    @ExcelProperty(value = "解析")
    private String analysis;
}
