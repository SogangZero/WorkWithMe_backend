package com.wwme.wwme.task.controller;

import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.service.TagCRUDService;
import com.wwme.wwme.task.service.TaskCRUDService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tag")
public class TagController {

    private TaskCRUDService taskCRUDService;
    private TagCRUDService tagCRUDService;


    @PostMapping("/")
    public ResponseEntity<Object> createTag(@RequestBody TagDTO tagDTO){
        tagCRUDService.createUpdateTag(tagDTO);

        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }

    @PutMapping("/")
    public ResponseEntity<Object> updateTag(@RequestBody TagDTO tagDTO){
        tagCRUDService.createUpdateTag(tagDTO);

        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }

    //TODO: coordinate with Group database
    @GetMapping("/")
    public ResponseEntity<Object> getTagList(@ModelAttribute TagDTO tagDTO){
        return null;
    }

    @DeleteMapping("/")
    public ResponseEntity<Map<String, Boolean>> deleteTag(@ModelAttribute Long tag_id){
        tagCRUDService.deleteTag(tag_id);
        return ResponseEntity.ok(Collections.singletonMap("success",true));
    }


}
