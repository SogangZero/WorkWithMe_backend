package com.wwme.wwme.task.service;

import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagCRUDService {
    public TagRepository tagRepository;
    public GroupRepository groupRepository;

    public Tag createUpdateTag(TagDTO tagDTO){
        Tag tag = convertTagDTOToTag(tagDTO);
        return tagRepository.save(tag);
    }

    public void deleteTag(TagDTO tagDTO){
        Tag tag = convertTagDTOToTag(tagDTO);

        if(tag.getId() == null){
            throw new RuntimeException("Tag Id Missing in deleteTag");
        }
        tagRepository.delete(tag);
    }

    public Tag convertTagDTOToTag(TagDTO tagDTO){
        Tag tag = new Tag();

        if(tagDTO.getId() != null){
            tag.setId(tagDTO.getId());
        }
        tag.setTag_name(tagDTO.getTag_name());
        tag.setGroup(groupRepository.findById(tagDTO.getGroup_id()).orElseThrow(()-> new RuntimeException("Could not find group Id for tagDTO")));

        return tagRepository.save(tag);
    }

    public TagDTO convertTagToTagDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setGroup_id(tag.getGroup().getId());
        tagDTO.setTag_name(tag.getTag_name());
        tagDTO.setId(tag.getId());

        return tagDTO;
    }
}
