package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask,Long> {
}
