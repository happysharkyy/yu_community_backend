package com.douyuehan.doubao.model.dto;

import lombok.Data;

@Data
public class CommentsReplyDTO {
    private static final long serialVersionUID = -5957433707110390852L;

    private String to_id;
    private String comment_id;
    /**
     * 内容
     */
    private String content;
}
