package com.wwme.wwme.task.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.service.TagCRUDService;
import com.wwme.wwme.task.service.TaskCRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tag")
public class TagController {

    private TaskCRUDService taskCRUDService;
    private TagCRUDService tagCRUDService;

    @Autowired
    public TagController(TaskCRUDService taskCRUDService, TagCRUDService tagCRUDService) {
        this.taskCRUDService = taskCRUDService;
        this.tagCRUDService = tagCRUDService;
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody TagDTO tagDTO){
        try {
            Tag tag = tagCRUDService.createUpdateTag(tagDTO);
            return ResponseEntity.ok(null); //TODO: what to send when data is null?
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTag(@RequestBody TagDTO tagDTO){
        try {
            tagCRUDService.createUpdateTag(tagDTO);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }
    }

    //TODO: coordinate with Group database
    @GetMapping
    public ResponseEntity<?> getTagList(@RequestParam Long group_id){
        try {
            TagDTO tagDTO = new TagDTO();
            tagDTO.setGroupId(group_id);
            List<TagListReadSendDTO> tagListReadSendDTOList = tagCRUDService.getTagList(tagDTO);
            return ResponseEntity.ok(new DataWrapDTO(tagListReadSendDTOList));
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }

    }

    @DeleteMapping
    public ResponseEntity<?> deleteTag(@RequestParam Long tag_id){
        try {
            tagCRUDService.deleteTag(tag_id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("error",e.getMessage()));
        }
    }


}
