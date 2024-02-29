package com.zl.gd.model;

import lombok.Data;

/**
 * @author: z.l
 * @date: 2023-05-19
 **/
@Data
public class ImageOrVideoResponse {
    private String code;
    private String msg;
    private ImageOrVideoModel data;
}
