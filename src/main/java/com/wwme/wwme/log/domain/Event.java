package com.wwme.wwme.log.domain;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@Entity
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

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), nullable = true)
    private Group group;

    private OperationType operationTypeEnum;
    private String operationString;
    private LocalDateTime operationTime;
}
