package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Task_Repository extends JpaRepository<Task, Long> {

}
