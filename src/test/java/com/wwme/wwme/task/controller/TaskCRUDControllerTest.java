package com.wwme.wwme.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.ResolverConfig;
import com.wwme.wwme.login.service.JWTUtilService;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskCRUDController.class)
@Import({SecurityTestConfig.class, ResolverConfig.class})
@WithMockUser(username = "TEST", roles = "USER")
public class TaskCRUDControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskCRUDService taskCRUDService;

    @MockBean
    private JWTUtilService jwtUtilService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();  // assuming a proper constructor/setters
        taskDTO.setTaskName("newTask1");

        Task task = new Task();
        task.setTaskName("newTask1");
        task.setId(1L);

//        when(taskCRUDService.createUpdateTask(any(TaskDTO.class))).thenReturn(task);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
