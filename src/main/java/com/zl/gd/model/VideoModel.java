package com.zl.gd.model;

import lombok.Data;

/**
 * @author: z.l
 * @date: 2023-05-19
 **/
@Data
public class VideoModel {
    //            private String atlas_id": 2927,
//            private String atlas_name":
//            private String atlas_cover": "https://imgs.8w6f.com/adminUpload/atlascover/20210414/png/1618352805684058.png",
//            private String atlas_long": 24,
//            private String watch_count": 19867,
//            private String watch_count_cycle": 20,
//            private String collect_count_cycle": 0,
//            private String push_time": 1618352805
    private int movieId;
    private String movieName;
    private String movieCover;
}

