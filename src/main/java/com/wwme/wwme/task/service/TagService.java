package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.DTO.tagReceiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.tagReceiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.taskSendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(CreateTagReceiveDTO createTagReceiveDTO);

    Tag updateTag(UpdateTagReceiveDTO updateTagReceiveDTO);

    void deleteTag(Long tag_id);

    List<TagListReadSendDTO> getTagList(Long group_id);
}
