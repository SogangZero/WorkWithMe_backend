package com.wwme.wwme.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.group.service.UserGroupService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserGroupService userGroupService;

    @Test
    void contextLoads() {
        assertThat(groupController).isNotNull();
    }

    @Test
    void groupCreateSuccess() throws Exception {
        long groupId = 0L;
        String groupName = "group_name";
        String groupColor = "FFFFFF";
        String jwtString = "exampleJWTString";

        GroupCreateRequestDTO requestDTO =
                new GroupCreateRequestDTO(groupName, groupColor);

        GroupCreateSuccessResponseDTO responseDTO =
                new GroupCreateSuccessResponseDTO(true, groupId, "temptemp");

        final String request = objectMapper.writeValueAsString(requestDTO);
        final String response = objectMapper.writeValueAsString(responseDTO);

        User mockedUser = new User();
        when(userService.getUserFromJWTString(jwtString))
                .thenReturn(mockedUser);

        Group mockedGroup = new Group();
        mockedGroup.setGroupName(requestDTO.getGroupName());
        mockedGroup.setId(groupId);
        when(groupService.createGroupWithUserAndColor(groupName, mockedUser, groupColor))
                .thenReturn(mockedGroup);

        mockMvc.perform(post("/group")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response));
    }


    @Test
    void groupUpdateSuccess() throws Exception {
        // Create objects and Mock dependencies
        long groupId = 0L;
        String groupName = "group_name";
        String groupColor = "FFFFFF";
        String jwtString = "exampleJWTString";

        GroupUpdateRequestDTO requestDTO = new GroupUpdateRequestDTO();
        requestDTO.setGroupId(groupId);
        requestDTO.setGroupColor(groupColor);
        requestDTO.setGroupName(groupName);

        GroupUpdateSuccessResponseDTO responseDTO = new GroupUpdateSuccessResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setGroupId(groupId);

        User mockedUser = new User();
        mockedUser.setId(0L);
        when(userService.getUserFromJWTString(jwtString))
                .thenReturn(mockedUser);


        Group mockedGroup = new Group();
        mockedGroup.setGroupName(groupName);
        mockedGroup.setId(groupId);

        UserGroup userGroup = new UserGroup();
        userGroup.setId(0L);
        userGroup.setGroup(mockedGroup);
        userGroup.setUser(mockedUser);
        userGroup.setColor(groupColor);

        ArrayList<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(userGroup);
        mockedGroup.setUserGroupList(userGroups);

        when(groupService.updateGroupNameAndColor(
                groupId,
                groupName,
                groupColor,
                mockedUser
        )).thenReturn(mockedGroup);

        // Expected input and output
        String request = objectMapper.writeValueAsString(requestDTO);
        String response = objectMapper.writeValueAsString(responseDTO);

        mockMvc.perform(put("/group")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response));


    }

    @Test
    void groupUpdateFail_AnyExceptionThrown() throws Exception {
        // Create objects and Mock dependencies
        long groupId = 0L;
        String groupName = "group_name";
        String groupColor = "FFFFFF";
        String jwtString = "exampleJWTString";

        GroupUpdateRequestDTO requestDTO = new GroupUpdateRequestDTO();
        requestDTO.setGroupId(groupId);
        requestDTO.setGroupColor(groupColor);
        requestDTO.setGroupName(groupName);

        GroupUpdateFailResponseDTO responseDTO = new GroupUpdateFailResponseDTO();
        responseDTO.setSuccess(false);

        when(userService.getUserFromJWTString(jwtString))
                .thenThrow(NoSuchElementException.class);

        String request = objectMapper.writeValueAsString(requestDTO);
        String response = objectMapper.writeValueAsString(responseDTO);

        mockMvc.perform(put("/group")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(response));
    }

}