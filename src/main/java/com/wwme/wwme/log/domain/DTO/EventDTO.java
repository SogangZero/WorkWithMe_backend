package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class EventDTO {
    private User user;
    private OperationType operationTypeEnum;
    private LocalDateTime operationTime;



    //받은 정보를 바탕으로 operationString 을 계산한다.
    public abstract String getOperationStr();
}
