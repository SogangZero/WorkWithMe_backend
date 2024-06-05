package com.wwme.wwme.task.service;

import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.repository.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagCRUDService {
    public final TagRepository tagRepository;
    public final GroupRepository groupRepository;
    public final EntityManager entityManager;

    public TagDTO readOneTag(Long tag_id){
        Tag tag = tagRepository.findById(tag_id).orElseThrow(()-> new EntityNotFoundException("Tag from Tag ID not found"));

        return convertTagToTagDTO(tag);
    }

    public Tag createUpdateTag(TagDTO tagDTO){
        Tag tag = convertTagDTOToTag(tagDTO);
        return tagRepository.save(tag);
    }

    public void deleteTag(Long tag_id){
        tagRepository.deleteById(tag_id);
    }

    public Tag convertTagDTOToTag(TagDTO tagDTO){
        Tag tag = new Tag();

        if(tagDTO.getId() != null){
            tag.setId(tagDTO.getId());
        }
        tag.setTagName(tagDTO.getTagName());
        if(tagDTO.getGroupId() != null){
            tag.setGroup(groupRepository.findById(tagDTO.getGroupId()).orElseThrow(()-> new RuntimeException("Could not find group Id for tagDTO")));
        }

        return tagRepository.save(tag);
    }

    public TagDTO convertTagToTagDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        if(tag.getGroup() != null){
            tagDTO.setGroupId(tag.getGroup().getId());
        }
        tagDTO.setTagName(tag.getTagName());
        tagDTO.setId(tag.getId());

        return tagDTO;
    }

    public Tag refreshAndReturnTag(Long tagId){
        Tag tag = entityManager.find(Tag.class,tagId);
        if(tag != null){
            entityManager.refresh(tag);
        }
        return entityManager.find(Tag.class,tagId);
    }

    public List<TagListReadSendDTO> getTagList(TagDTO tagDTO){

        List<Tag> tagList= tagRepository.findAllByGroupId(tagDTO.getGroupId());

        List<TagListReadSendDTO> tagListReadSendDTOList = new ArrayList<>();

        for(Tag tag : tagList){
            TagListReadSendDTO tagListReadSendDTO = new TagListReadSendDTO();
            tagListReadSendDTO.setTag_id(tag.getId());
            tagListReadSendDTO.setTag_name(tag.getTagName());

            tagListReadSendDTOList.add(tagListReadSendDTO);
        }

        return tagListReadSendDTOList;
    }
}
