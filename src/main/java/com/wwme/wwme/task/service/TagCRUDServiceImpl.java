package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CreateTagSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
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
    private final TaskRepository taskRepository;

    @Override //TODO: is this check algorithm for group necessary?
    public CreateTagSendDTO createTag(CreateTagReceiveDTO createTagReceiveDTO){
        Tag tag = new Tag();

        if(createTagReceiveDTO.getTag_name() == null || createTagReceiveDTO.getTag_name().isBlank()){
            throw new IllegalArgumentException(
                    "The passed tag name was null or empty");
        }

        Group group = groupRepository.findById(createTagReceiveDTO.getGroup_id()).orElseThrow(()-> new NoSuchElementException(
                "Could not find Group with ID: " + createTagReceiveDTO.getGroup_id() +
                " in method createTag. Details: " + createTagReceiveDTO.toString()));

        tag.setGroup(group);
        tag.setTagName(createTagReceiveDTO.getTag_name());

        Tag newTag = tagRepository.save(tag);

        return CreateTagSendDTO.builder()
                .tag_id(newTag.getId())
                .tag_name(newTag.getTagName())
                .build();


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
        Tag tag = tagRepository.findById(tag_id).orElseThrow(()->new NoSuchElementException(
                "Could not find Tag with tag_id : "+tag_id+
                "In function deleteTag"
        ));

        for(Task task : tag.getTaskList()){
            task.setTag(null);
            taskRepository.save(task);
        }
        tagRepository.deleteById(tag_id);
    }


    @Override
    public List<TagListReadSendDTO> getTagList(Long group_id){
        groupRepository.findById(group_id).orElseThrow(()->
                new NoSuchElementException("Could not find group with group_id : "
                +group_id + "in method getTagList."));
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
