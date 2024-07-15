package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.log.domain.DTO.tag.CreateTagLogDTO;
import com.wwme.wwme.log.domain.DTO.tag.DeleteTagLogDTO;
import com.wwme.wwme.log.domain.DTO.tag.UpdateTagNameLogDTO;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.log.service.EventService;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CreateTagSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final EventService eventService;

    @Override //TODO: is this check algorithm for group necessary?
    public CreateTagSendDTO createTag(CreateTagReceiveDTO createTagReceiveDTO, User loginUser){
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

        //log feature
        CreateTagLogDTO createTagLogDTO = CreateTagLogDTO.buildWithSpecificParamsNoID()
                .task(null)
                .user(loginUser)
                .operationTime(LocalDateTime.now())
                .operationTypeEnum(OperationType.CREATE_TAG)
                .group(group)
                .createTagName(tag.getTagName())
                .build();

        eventService.createEvent(createTagLogDTO);

        return CreateTagSendDTO.builder()
                .tag_id(newTag.getId())
                .tag_name(newTag.getTagName())
                .build();
    }

    @Override
    public Tag updateTag(UpdateTagReceiveDTO updateTagReceiveDTO, User loginUser){
        Tag tag = tagRepository.findById(updateTagReceiveDTO.getTag_id()).orElseThrow(()-> new NoSuchElementException(
                "Could not find Tag with ID: " + updateTagReceiveDTO.getTag_id() +
                " in method updateTag. Details: " + updateTagReceiveDTO.toString()));
        String prevName = tag.getTagName();
        String afterName = updateTagReceiveDTO.getTag_name();

        tag.setTagName(updateTagReceiveDTO.getTag_name());
        Tag updatedTag = tagRepository.save(tag);

        //log feature

        UpdateTagNameLogDTO updateTagNameLogDTO = UpdateTagNameLogDTO
                .buildWithSpecificParamsNoID()
                .prevTagName(prevName)
                .changedTagName(afterName)
                .operationTime(LocalDateTime.now())
                .operationTypeEnum(OperationType.UPDATE_TAG_NAME)
                .task(null)
                .user(loginUser)
                .group(tag.getGroup())
                .build();

        eventService.createEvent(updateTagNameLogDTO);

        return updatedTag;
    }

    @Override
    public void deleteTag(Long tag_id, User loginUser){
        Tag tag = tagRepository.findById(tag_id).orElseThrow(()->new NoSuchElementException(
                "Could not find Tag with tag_id : "+tag_id+
                "In function deleteTag"
        ));

        for(Task task : tag.getTaskList()){
            task.setTag(null);
            taskRepository.save(task);
        }
        Group groupForLog = tag.getGroup();
        String tagNameForLog = tag.getTagName();

        tagRepository.deleteById(tag_id);

        DeleteTagLogDTO deleteTagLogDTO = DeleteTagLogDTO
                .buildWithSpecificParamsNoID()
                .task(null)
                .group(groupForLog)
                .user(loginUser)
                .operationTime(LocalDateTime.now())
                .operationTypeEnum(OperationType.DELETE_TAG)
                .deletedTagName(tagNameForLog)
                .build();

        eventService.createEvent(deleteTagLogDTO);
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
