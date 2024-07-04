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
public class UpdateTaskDeleteTag extends EventDTO{
    private Tag deletedTag;
    @Override
    public String getOperationStr() {
        return deletedTag.getTagName();
    }
}
