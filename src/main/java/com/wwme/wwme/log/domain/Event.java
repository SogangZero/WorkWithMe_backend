package com.wwme.wwme.log.domain;

import com.wwme.wwme.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Event {
    private Long id;
    private User user;
    private OperationType operationTypeEnum;
    private String operationString;
    private LocalDateTime operationTime;
}
