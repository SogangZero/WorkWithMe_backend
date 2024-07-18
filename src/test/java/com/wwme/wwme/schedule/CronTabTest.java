package com.wwme.wwme.schedule;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.group.service.UserGroupService;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTagReceiveDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.service.TagService;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class CronTabTest {
    @Autowired GroupRepository groupRepository;
    @Autowired UserRepository userRepository;
    @Autowired TagRepository tagRepository;
    @Autowired TaskRepository taskRepository;
    @Autowired GroupService groupService;
    @Autowired UserGroupService userGroupService;
    @Autowired TaskCRUDService taskCRUDService;
    @Autowired TagService tagService;
    @Autowired CronTab cronTab;

    Group noUserGroup;
    Group someUserGroup;
    User userInsideGroup;
    User userThatLeavedGroup;

    @BeforeEach
    void setUp() {
        userInsideGroup = userRepository.save(User.builder()
                .nickname("userInsideGroup")
                .profileImageId(1L)
                .build()
        );

        someUserGroup = groupService.createGroupWithUserAndColor(
                 "someUserGroup", userInsideGroup, 1L
        );

        userThatLeavedGroup = userRepository.save(User.builder()
                .nickname("userThatLeavedGroup")
                .profileImageId(1L)
                .build()
        );

        noUserGroup = groupService.createGroupWithUserAndColor(
                "noUserGroup", userThatLeavedGroup, 1L
        );

        var someTagDTO = new CreateTagReceiveDTO();
        someTagDTO.setTag_name("ASDASD");
        someTagDTO.setGroup_id(someUserGroup.getId());
        tagService.createTag(someTagDTO, userInsideGroup);
        taskCRUDService.createTask("Normal", null, "anyone", null, someUserGroup.getId(), null, userInsideGroup);

        var tagDTO = new CreateTagReceiveDTO();
        tagDTO.setTag_name("asd");
        tagDTO.setGroup_id(noUserGroup.getId());
        tagService.createTag(tagDTO, userThatLeavedGroup);
        taskCRUDService.createTask("hanging task", null, "anyone", null, noUserGroup.getId(), null, userThatLeavedGroup);

        // leave group
        userThatLeavedGroup = userRepository.findById(userThatLeavedGroup.getId()).get();
        userGroupService.removeUserFromGroup(noUserGroup.getId(), userThatLeavedGroup);
    }

    @Test
    @Transactional
    void emptyGroupRemoveTest() {
        cronTab.removeEmptyGroup();

        var groupCount = groupRepository.count();
        var tagCount = tagRepository.count();
        var taskCount = taskRepository.count();
        assertThat(groupCount).isEqualTo(1);
        assertThat(tagCount).isEqualTo(1);
        assertThat(taskCount).isEqualTo(1);
    }
}
