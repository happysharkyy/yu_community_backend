package com.douyuehan.doubao.model.dto;

import lombok.Data;

import java.util.List;
@Data
public class RankDTO {
    String alias;
    int count;
    List<String> key;
    List<String> result;
}
