package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.repository.TagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TagCRUDServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;
    private final EntityManager entityManager;


    @Override //TODO: is this check algorithm for group necessary?
    public Tag createTag(CreateTagReceiveDTO createTagReceiveDTO){
        Tag tag = new Tag();

        Group group = groupRepository.findById(createTagReceiveDTO.getGroup_id()).orElseThrow(()-> new NoSuchElementException(
                "Could not find Group with ID: " + createTagReceiveDTO.getGroup_id() +
                " in method createTag. Details: " + createTagReceiveDTO.toString()));

        tag.setGroup(group);
        tag.setTagName(createTagReceiveDTO.getTag_name());

        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(UpdateTagReceiveDTO updateTagReceiveDTO){
        Tag tag = tagRepository.findById(updateTagReceiveDTO.getTag_id()).orElseThrow(()-> new NoSuchElementException(
                "Could not find Tag with ID: " + updateTagReceiveDTO.getTag_id() +
                " in method updateTag. Details: " + updateTagReceiveDTO.toString()));

        tag.setTagName(updateTagReceiveDTO.getTag_name());
        return tagRepository.save(tag);
    }

    @Override
    public void deleteTag(Long tag_id){
        tagRepository.deleteById(tag_id);
    }


    @Override
    public List<TagListReadSendDTO> getTagList(Long group_id){
        List<Tag> tagList= tagRepository.findAllByGroupId(group_id);
        List<TagListReadSendDTO> tagListReadSendDTOList = new ArrayList<>();

        for(Tag tag : tagList){
            TagListReadSendDTO tagListReadSendDTO = TagListReadSendDTO.builder()
                    .tag_id(tag.getId())
                    .tag_name(tag.getTagName())
                    .build();
            tagListReadSendDTOList.add(tagListReadSendDTO);
        }

        return tagListReadSendDTOList;
    }
}
