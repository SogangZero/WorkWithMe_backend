package com.wwme.wwme.log.domain;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "event_table")
public class Event {

    @Builder
    public Event(User user, OperationType operationTypeEnum,
                 String operationString, LocalDateTime operationTime) {
        this.user = user;
        this.operationTypeEnum = operationTypeEnum;
        this.operationString = operationString;
        this.operationTime = operationTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @Enumerated(EnumType.STRING)
    private OperationType operationTypeEnum;

    private String operationString;
    private LocalDateTime operationTime;
}
