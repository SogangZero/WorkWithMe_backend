package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.User_task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTaskRepository extends JpaRepository<User_task,Long> {
}
