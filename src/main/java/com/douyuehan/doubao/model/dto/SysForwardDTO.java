package com.douyuehan.doubao.model.dto;

import lombok.Data;

@Data
public class SysForwardDTO {
    private String objId;

    private String userId;

    private String content;

    private Integer forwardStatus;

    private String type;
}
