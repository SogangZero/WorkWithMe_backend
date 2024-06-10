package com.wwme.wwme.task.domain.DTO.sendDTO;

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
