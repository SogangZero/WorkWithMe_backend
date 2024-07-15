package com.wwme.wwme.task.service;

import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CreateTagSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;

import java.util.List;

public interface TagService {
    CreateTagSendDTO createTag(CreateTagReceiveDTO createTagReceiveDTO);

    Tag updateTag(UpdateTagReceiveDTO updateTagReceiveDTO);

    void deleteTag(Long tag_id);

    List<TagListReadSendDTO> getTagList(Long group_id);
}
