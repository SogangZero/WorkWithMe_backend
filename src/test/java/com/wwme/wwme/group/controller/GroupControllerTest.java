package com.wwme.wwme.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwme.wwme.group.DTO.GroupCreateRequestDTO;
import com.wwme.wwme.group.DTO.GroupCreateSuccessResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GroupController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class GroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupController groupController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        assertThat(groupController).isNotNull();
    }

    @Test
    void groupCreateSuccess() throws Exception{

        GroupCreateRequestDTO requestDTO =
                new GroupCreateRequestDTO("group_name", "FFFFFF");

        GroupCreateSuccessResponseDTO responseDTO =
                new GroupCreateSuccessResponseDTO(true, 0, "temptemp");

        final String request = objectMapper.writeValueAsString(requestDTO);
        final String response = objectMapper.writeValueAsString(responseDTO);

        mockMvc.perform(post("/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }

}