package com.wwme.wwme.task.controller;

import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.service.TagCRUDService;
import com.wwme.wwme.task.service.TaskCRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Collections;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private TaskCRUDService taskCRUDService;
    private TagCRUDService tagCRUDService;


    @PostMapping("/")
    public ResponseEntity<Object> createTag(TagDTO tagDTO){
        tagCRUDService.createUpdateTag(tagDTO);

        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }

    @PutMapping("/")
    public ResponseEntity<Object> updateTag(TagDTO tagDTO){
        tagCRUDService.createUpdateTag(tagDTO);

        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }




}
