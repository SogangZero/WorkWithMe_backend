package com.wwme.wwme.schedule;

import com.wwme.wwme.notification.NotificationService;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.service.TaskCRUDService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSchedule {
    private final TaskCRUDService taskCRUDService;
    private final NotificationService notificationService;
    @Scheduled(cron = "${cron.on-due-date}")
    public void sendOnDueDateNotifications() {
        try {
            var userTasks = taskCRUDService.findAllTodayDueDateTasks();
            userTasks.forEach((userTask) -> {
                log.info("Sending Due Date Notification for userTask: {}", userTask);
                Task task = userTask.getTask();
                notificationService.sendDueDateNotification(task, userTask.getUser());
            });
        }
        catch (Exception e) {
            log.error("Error in sending on due date notifications", e);
        }
    }
}
