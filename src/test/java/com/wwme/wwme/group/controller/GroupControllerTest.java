package com.wwme.wwme.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import com.wwme.wwme.group.DTO.*;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.service.GroupInvitationService;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.group.service.UserGroupService;
import com.wwme.wwme.login.config.SecurityTestConfig;
import com.wwme.wwme.login.config.WebConfig;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import com.wwme.wwme.user.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GroupController.class
        //excludeAutoConfiguration = SecurityTestConfig.class
)
@WithMockUser(username = "test", roles = "USER")
@Import({SecurityTestConfig.class, WebConfig.class})
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

    @MockBean
    private GroupInvitationService groupInvitationService;

    @MockBean
    private UserRepository userRepository;

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
        String invitationCode = "invCode";

        GroupCreateRequestDTO requestDTO =
                new GroupCreateRequestDTO(groupName, groupColor);

        GroupCreateSuccessResponseDTO responseDTO =
                new GroupCreateSuccessResponseDTO(
                        true,
                        groupId,
                        "/group/invitation/" + invitationCode
                );

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

        when(groupInvitationService.createGroupInvitation(mockedGroup))
                .thenReturn(invitationCode);
        mockMvc.perform(post("/group")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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
        mockedUser.setUserGroup(userGroups);

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
                .andExpect(status().isBadRequest())
                .andExpect(content().json(response));
    }

    @Test
    void groupReadAllOfUserSuccess() throws Exception {
        // Create objects and Mock dependencies
        long groupId1 = 0L;
        long groupId2 = 1L;
        String groupName1 = "group_name1";
        String groupColor1 = "FFFFFF";
        String groupName2 = "group_name2";
        String groupColor2 = "AAAAAA";
        String jwtString = "exampleJWTString";

        String response = """
                {
                    "success": true,
                    "group_list": [
                        {
                            "group_id": 0,
                            "group_name": "group_name1",
                            "group_color": "FFFFFF"
                        },
                        {
                            "group_id": 1,
                            "group_name": "group_name2",
                            "group_color": "AAAAAA"
                        }
                    ]
                }
                """;

        User mockedUser = new User();
        mockedUser.setId(0L);
        when(userService.getUserFromJWTString(jwtString))
                .thenReturn(mockedUser);


        Group mockedGroup1 = new Group();
        mockedGroup1.setGroupName(groupName1);
        mockedGroup1.setId(groupId1);

        Group mockedGroup2 = new Group();
        mockedGroup2.setGroupName(groupName2);
        mockedGroup2.setId(groupId2);

        UserGroup userGroup1 = new UserGroup();
        userGroup1.setId(0L);
        userGroup1.setGroup(mockedGroup1);
        userGroup1.setUser(mockedUser);
        userGroup1.setColor(groupColor1);

        UserGroup userGroup2 = new UserGroup();
        userGroup2.setId(1L);
        userGroup2.setGroup(mockedGroup2);
        userGroup2.setUser(mockedUser);
        userGroup2.setColor(groupColor2);

        ArrayList<UserGroup> userGroups = new ArrayList<>();
        userGroups.add(userGroup1);
        userGroups.add(userGroup2);


        when(userGroupService.getAllUserGroupOfUser(mockedUser))
                .thenReturn(userGroups);

        mockMvc.perform(get("/group/all")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(response));


    }

    @Test
    void groupReadAllOfUserFail() throws Exception {
        String jwtString = "somejwtstring";

        String response = """
                {"success": false}
                """;

        when(userService.getUserFromJWTString(jwtString))
                .thenThrow(NoSuchElementException.class);

        mockMvc.perform(get("/group/all")
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().json(response));

    }

    @Test
    void groupReadSuccess() throws Exception {
        String jwtString = "somejwtstring";
        String groupName = "group_name";
        String groupColor = "ABCABC";
        String groupId = "0";
        String response = """
                    {
                        "success": true,
                        "group_name": "group_name",
                        "group_color": "ABCABC",
                        "user": [{"user_id": 0}]
                    }
                """;

        User mockedUser = new User();
        mockedUser.setId(0L);
        when(userService.getUserFromJWTString(jwtString))
                .thenReturn(mockedUser);

        Group mockedGroup = new Group();
        mockedGroup.setId(0L);
        mockedGroup.setGroupName(groupName);

        UserGroup userGroup = new UserGroup();
        userGroup.setGroup(mockedGroup);
        userGroup.setUser(mockedUser);
        userGroup.setColor(groupColor);
        when(userGroupService.getUserGroupByIdAndUser(mockedGroup.getId(), mockedUser))
                .thenReturn(userGroup);

        mockMvc.perform(get("/group")
                        .param("group_id", groupId)
                        .cookie(new Cookie("Authorization", jwtString))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(response));
    }
}