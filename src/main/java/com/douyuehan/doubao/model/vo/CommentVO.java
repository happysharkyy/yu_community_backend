package com.douyuehan.doubao.model.vo;

import lombok.Data;

import java.util.Date;


@Data
public class  CommentVO {

    private String id;

    private String type;

    private String content;

    private String topicId;

    private String userId;

    private String username;

    private Date createTime;

    private int isDel;

}
