package com.wwme.wwme.task.domain.DTO.sendDTO;

import com.wwme.wwme.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor

public class ReadOneTaskSendDTO {
    private Long taskId;
    private String taskName;
    private Integer isDoneCount;
    private Integer totalUserCount;
    private List<User> userList;
    private String taskType;
    private String groupName;
    private LocalDate startTime;
    private LocalDate endTime;
}
