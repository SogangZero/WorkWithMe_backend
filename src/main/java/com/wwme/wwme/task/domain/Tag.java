package com.wwme.wwme.task.domain;

import com.wwme.wwme.group.domain.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;

    @OneToMany(mappedBy = "tag")
    private List<Task> taskList = new ArrayList<>();


    public void addToTaskList(Task task){
        taskList.add(task);
    }

    public void deleteFromTaskList(Long taskId){
        Iterator<Task> it = taskList.iterator();

        while(it.hasNext()){
            Task t =  (Task) it.next();
            if(Objects.equals(t.getId(), taskId)){
                it.remove();
                break;
            }
        }
    }
}
