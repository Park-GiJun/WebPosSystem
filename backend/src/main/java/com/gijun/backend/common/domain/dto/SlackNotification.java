package com.gijun.backend.common.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackNotification {
    private String status;
    private String message;
    private String timestamp;
}
