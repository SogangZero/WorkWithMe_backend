package com.wwme.wwme.schedule;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collection;


@Slf4j
@Component
@RequiredArgsConstructor
public class CronTab {
    private final GroupRepository groupRepository;

    @Scheduled(cron = "${cron.remove-empty-group}")
    public void removeEmptyGroup() {
        try {
            log.info("Starting delete empty groups");
            Collection<Group> emptyGroups = groupRepository.findEmptyGroups();
            groupRepository.deleteAll(emptyGroups);
        }
        catch (Exception e) {
            log.error("Error at removeEmptyGroup", e);
        }
    }
}
