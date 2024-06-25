package com.wwme.wwme.task.domain.DTO.receiveDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MakeTaskDoneReceiveDTO {
    Long task_id;
    Boolean done;
}
