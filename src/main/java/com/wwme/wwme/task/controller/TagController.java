package com.wwme.wwme.task.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTagReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.CreateTagSendDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.DataResponseDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.TagListReadSendDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.service.TagCRUDServiceImpl;
import com.wwme.wwme.task.service.TaskCRUDService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tag")
@Slf4j
public class TagController {

    private final TaskCRUDService taskCRUDService;
    private final TagCRUDServiceImpl tagCRUDServiceImpl;

    @Autowired
    public TagController(TaskCRUDService taskCRUDService, TagCRUDServiceImpl tagCRUDServiceImpl) {
        this.taskCRUDService = taskCRUDService;
        this.tagCRUDServiceImpl = tagCRUDServiceImpl;
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody CreateTagReceiveDTO createTagReceiveDTO){
        try {
            CreateTagSendDTO createTagSendDTO = tagCRUDServiceImpl.createTag(createTagReceiveDTO);
            return new ResponseEntity<>(new DataResponseDTO(createTagSendDTO), HttpStatus.OK); //TODO: what to send when data is null?
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTag(@RequestBody UpdateTagReceiveDTO updateTagReceiveDTO){
        try {
            tagCRUDServiceImpl.updateTag(updateTagReceiveDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }
    }

    //TODO: coordinate with Group database
    @GetMapping
    public ResponseEntity<?> getTagList(@RequestParam Long group_id){
        try {
            List<TagListReadSendDTO> tagListReadSendDTOList = tagCRUDServiceImpl.getTagList(group_id);
            return ResponseEntity.ok(new DataWrapDTO(tagListReadSendDTOList));
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);        }

    }

    @DeleteMapping
    public ResponseEntity<?> deleteTag(@RequestParam Long tag_id){
        try {
            tagCRUDServiceImpl.deleteTag(tag_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);        }
    }


}
