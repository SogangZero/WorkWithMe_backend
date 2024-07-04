package com.wwme.wwme.log.domain.DTO;

import com.wwme.wwme.task.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskChangeTag extends EventDTO{

    private Tag previousTag;
    private Tag updatedTag;

    @Override
    public String getOperationStr() {
        return previousTag.getTagName() + "|" + updatedTag.getTagName();
    }
}
