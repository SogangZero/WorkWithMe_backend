package com.wwme.wwme.task.domain.DTO.taskSendDTO;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagListReadSendDTO {
    private Long tag_id;
    private String tag_name;
}
