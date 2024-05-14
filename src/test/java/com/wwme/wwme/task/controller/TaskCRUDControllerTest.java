package com.wwme.wwme.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;
@WebMvcTest(TaskCRUDController.class)
public class TaskCRUDControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskCRUDService taskCRUDService;

    @Test
    public void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();  // assuming a proper constructor/setters
        taskDTO.setTask_name("newTask1");

        Task task = new Task();
        task.setTask_name("newTask1");
        task.setId(1L);

        when(taskCRUDService.createUpdateTask(any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(post("/task/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
