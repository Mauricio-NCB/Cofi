package com.website.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponseDTO {

    private Integer id;
    private Integer userId;
    private String userName;
    private String content;
    private String timestamp;
}
