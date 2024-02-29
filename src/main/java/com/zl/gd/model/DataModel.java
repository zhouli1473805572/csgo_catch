package com.zl.gd.model;

import lombok.Data;

import java.util.List;

/**
 * @author: z.l
 * @date: 2023-05-19
 **/
@Data
public class DataModel {
    private List<ImageOrVideoModel> data;
    private Integer total;

}
