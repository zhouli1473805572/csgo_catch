package com.zl.gd.model;

import lombok.Data;

/**
 * @author: z.l
 * @date: 2023-05-19
 **/
@Data
public class Response {
    private String code;
    private String msg;
    private DataModel data;
}
