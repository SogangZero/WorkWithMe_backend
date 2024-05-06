package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupRepositoryTest {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupRepositoryTest(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @AfterEach
    void tearDown() {
        groupRepository.deleteAll();
    }

    @Test
    void groupSaveTest() {
        Group g1 = new Group("g1");
        g1 = groupRepository.save(g1);

        Group savedGroup = groupRepository.findById(g1.getId()).get();

        assertThat(savedGroup.getId()).isEqualTo(g1.getId());
    }

    @Test
    void groupRemoveTest() {
        Group g1 = new Group("g1");
        g1 = groupRepository.save(g1);

        groupRepository.delete(g1);

        long totalGroupCount = groupRepository.count();
        assertThat(totalGroupCount).isEqualTo(0);
    }

    @Test
    void groupFindByGroupNameTest() {
        Group g1 = new Group("g1");
        g1 = groupRepository.save(g1);

        Group foundGroup = groupRepository.findByGroupName("g1").get();
        assertThat(g1.getId()).isEqualTo(foundGroup.getId());
    }
}