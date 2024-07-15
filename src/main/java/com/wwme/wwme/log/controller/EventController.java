package com.wwme.wwme.log.controller;

import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.DTO.ErrorWrapDTO;
import com.wwme.wwme.log.domain.DTO.EventDTO;
import com.wwme.wwme.log.domain.DTO.ReceiveLogDTO;
import com.wwme.wwme.log.domain.DTO.ReturnLogDTO;
import com.wwme.wwme.log.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/log")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/list")
    public ResponseEntity<?> getLogListByGroup(
            @RequestParam(value = "last_id", required = false) Long last_id,
            @RequestParam(value = "group_id") Long group_id){

        try {
            ReceiveLogDTO receiveLogDTO = ReceiveLogDTO.builder()
                    .last_id(last_id)
                    .group_id(group_id)
                    .build();

            List<EventDTO> eventDTOList = eventService.readGroupEventsPaging(receiveLogDTO);

            List<ReturnLogDTO> returnLogDTOList = new ArrayList<>();
            for (EventDTO eventDTO : eventDTOList){
                returnLogDTOList.add(ReturnLogDTO.builder()
                        .log_id(eventDTO.getId())
                        .log_text(eventDTO.convertToString())
                        .task_id( eventDTO.getTask() == null ? null : eventDTO.getTask().getId())
                        .log_time(eventDTO.getOperationTime())
                        .is_task_log(eventDTO.isTaskLog())
                        .build());
            }
            return ResponseEntity.ok(new DataWrapDTO(returnLogDTOList));
        }catch (Exception e){
            log.error(e.getMessage());
            ErrorWrapDTO errorWrapDTO = new ErrorWrapDTO(e.getMessage());
            return ResponseEntity.badRequest().body(errorWrapDTO);
        }

    }

}
