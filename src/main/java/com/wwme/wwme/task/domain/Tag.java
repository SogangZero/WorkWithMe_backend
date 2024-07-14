package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @Builder.Default
    @OneToMany(mappedBy = "tag", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Task> taskList = new ArrayList<>();


    public void addToTaskList(Task task) {
        taskList.add(task);
        task.setTag(this);
    }

    public void deleteFromTaskList(Long taskId) {
        Iterator<Task> it = taskList.iterator();
        while (it.hasNext()) {
            Task t = it.next();
            if (Objects.equals(t.getId(), taskId)) {
                it.remove();
                t.setTag(null);
                break;
            }
        }
    }
}
