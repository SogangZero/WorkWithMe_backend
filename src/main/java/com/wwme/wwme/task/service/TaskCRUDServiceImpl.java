package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.dto.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskCRUDServiceImpl implements TaskCRUDService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;


    @Override
    public Task createTask(String taskName,
                           LocalDateTime endTime,
                           String taskType,
                           Long tagId,
                           Long groupId,
                           Long todoUserId,
                           User user) throws IllegalArgumentException{

        //parameter validation
        checkParameterValidity(taskName, endTime, taskType);
        Tag tag = getTagFromDB(tagId);
        Group group = getGroupFromDB(groupId);
        User todoUser = getTodoUserForPersonalTask(taskType, todoUserId);
        checkGroupIncludeTag(tag, group);
        checkUsersInSameGroup(taskType, user, group, todoUser);

        //엔티티 구성
        Task taskEntity = Task.builder()
                .taskName(taskName)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .taskType(taskType)
                .totalIsDone(false)
                .tag(tag)
                .group(group)
                .userTaskList(new ArrayList<>())
                .build();

        //userTask 추가
        addUserTaskByTaskType(taskType, group, taskEntity, todoUser);

        //DB에 추가
        return taskRepository.save(taskEntity);
    }

    private User getTodoUserForPersonalTask(String taskType, Long todoUserId) {
        User todoUser = null;
        if (taskType.equals("personal")) {
            todoUser = userRepository.findById(todoUserId).orElseThrow(() -> (
                new IllegalArgumentException("Create Task Fail - Not Found Todo User")
            ));
        }
        return todoUser;
    }

    private Group getGroupFromDB(Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> (
                new IllegalArgumentException("Create Task Fail - Not Found Group")
        ));
        return group;
    }

    private Tag getTagFromDB(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> (
                new IllegalArgumentException("Create Task Fail - Not Found Tag")
        ));
        return tag;
    }

    private static void checkParameterValidity(String taskName, LocalDateTime endTime, String taskType) {
        if (taskName == null || taskName.isEmpty()) {
            throw new IllegalArgumentException("Create Task Fail - No content of TaskName");
        }
        if (endTime == null || endTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Create Task Fail - EndTime is before now");
        }
        if (taskType == null || !(taskType.equals("group") || taskType.equals("personal") || taskType.equals("anyone"))) {
            throw new IllegalArgumentException("Create Task Fail - No matched Task Type");
        }
    }

    private static void checkGroupIncludeTag(Tag tag, Group group) {
        if (!tag.getGroup().getId().equals(group.getId())) {
            System.out.println("TaskCRUDServiceImpl.createTask");
            throw new IllegalArgumentException("Create Task Fail - tag and group Match Fail"
                    + "Tag Id" + tag.getId()
                    + "Group Id" + group.getId());
        }
    }

    private static void checkUsersInSameGroup(String taskType, User user, Group group, User todoUser) {
        boolean userExist = group.existUser(user);
        boolean todoUserExist = group.existUser(todoUser);

        if (taskType.equals("personal") && !(userExist && todoUserExist)) {
            throw new IllegalArgumentException("Create Task Fail - User or TodoUser Not In Group");
        }
    }

    private static void addUserTaskByTaskType(String taskType, Group group, Task taskEntity, User todoUser) {
        switch (taskType) {
            case "group" -> {
                for (UserGroup userGroup : group.getUserGroupList()) {
                    User groupMember = userGroup.getUser();
                    UserTask userTask = UserTask.builder()
                            .task(taskEntity)
                            .user(groupMember)
                            .isDone(false)
                            .build();
                    taskEntity.addUserTask(userTask);
                }
            }
            case "personal" -> {
                UserTask userTask = UserTask.builder()
                        .task(taskEntity)
                        .user(todoUser)
                        .isDone(false)
                        .build();
                taskEntity.addUserTask(userTask);
            }
            case "anyone" -> {
            }
            //anyone은 어떻게 UserTask 인자 넣지?
            default -> throw new IllegalArgumentException("Create Task Fail - No Matched Task Type");
        }
    }

    @Override
    public Task updateTask(Long taskId,
                           LocalDateTime endTime,
                           String taskType,
                           Long tagId,
                           Long todoUserId,
                           User user) {
        Task task = getTaskFromDB(taskId);
        User todoUser = getTodoUserFromDB(todoUserId);
        Tag tag = getTagFromDB(tagId);

        validateUsers(todoUserId, user, task);

        //업데이트
        updateTag(tag, task);
        updateEndTime(endTime, task);
        updateTaskType(taskType, task, todoUser);
        updateTodoUser(task, todoUser);

        return task;
    }

    private static void validateUsers(Long todoUserId, User user, Task task) {
        if (!task.getGroup().existUser(user) || !task.getGroup().existUserById(todoUserId)) {
            throw new IllegalArgumentException("Update Task Fail - Task Group Not Matched User Groups");
        }
    }

    private void updateTodoUser(Task task, User todoUser) {
        if (task.getTaskType().equals("personal") && !task.getUserTaskList().get(0).getUser().equals(todoUser)) {
            userTaskRepository.deleteByTask(task);
            UserTask userTask = UserTask.builder()
                    .user(todoUser)
                    .task(task)
                    .isDone(false)
                    .build();

            task.addUserTask(userTask);
        }
    }

    private void updateTaskType(String taskType, Task task, User todoUser) {
        if (taskType != null && !task.getTaskType().equals(taskType)) {
            changeTaskType(taskType, task, todoUser);
        }
    }

    private static void updateEndTime(LocalDateTime endTime, Task task) {
        if (endTime != null && !task.getEndTime().equals(endTime)) {
            if (endTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Update Task Fail - EndTime is before now");
            }
            task.changeEndTime(endTime);
        }
    }

    private static void updateTag(Tag tag, Task task) {
        if (!tag.equals(task.getTag())) {
            if (!task.validateTagIdInGroup(tag)) {
                throw new IllegalArgumentException("Update Task Fail - Tag Not Matched Group");
            }
            task.changeTag(tag);
        }
    }

    private User getTodoUserFromDB(Long todoUserId) {
        return userRepository.findById(todoUserId).orElseThrow(() -> (
                new IllegalArgumentException("Update Task Fail - Not Found TodoUser")
        ));
    }

    private Task getTaskFromDB(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> (
                new IllegalArgumentException("Update Task Fail - Not Task In DB")
        ));
    }

    private void changeTaskType(String taskType, Task task, User todoUser) {
        if (!task.validateTaskType(taskType)) {
            throw new IllegalArgumentException("Update Task Fail - TaskType is Invalid");
        }
        //personal -> group : personal 이외의 group에 속한 유저에 대해 UserTask 추가
        if (task.getTaskType().equals("personal") && taskType.equals("group")) {
            User existUser = task.getUserTaskList().get(0).getUser();
            for (UserGroup userGroup : task.getGroup().getUserGroupList()) {
                User addUser = userGroup.getUser();
                if (addUser.equals(existUser)) {
                    continue;
                }
                UserTask userTask = UserTask.builder()
                        .user(addUser)
                        .task(task)
                        .isDone(false)
                        .build();
                task.addUserTask(userTask);
            }

            task.changeTaskType("group");
        }
        //personal -> anyone : UserTask를 삭제해줌
        if (task.getTaskType().equals("personal") && taskType.equals("anyone")) {
            userTaskRepository.deleteByTask(task);
            task.changeTaskType("anyone");
        }

        //group -> personal : group원들의 UserTask를 todoUser를 제외하고 삭제
        if (task.getTaskType().equals("group") && taskType.equals("personal")) {
            userTaskRepository.deleteByTaskExceptForOnePerson(task, todoUser);
            task.changeTaskType("personal");
        }
        //group -> anyone : UserTask 들을 삭제해줌
        if (task.getTaskType().equals("group") && taskType.equals("anyone")) {
            userTaskRepository.deleteByTask(task);
            task.changeTaskType("anyone");
        }

        //anyone -> personal : personal에 대한 userTask를 추가해줌
        if (task.getTaskType().equals("anyone") && taskType.equals("personal")) {
            UserTask userTask = UserTask.builder()
                    .user(todoUser)
                    .task(task)
                    .isDone(false)
                    .build();
            task.addUserTask(userTask);

            task.changeTaskType("personal");
        }

        //anyone -> group : group원들의 UserTask를 추가해줌
        if (task.getTaskType().equals("anyone") && taskType.equals("group")) {
            for (UserGroup userGroup : task.getGroup().getUserGroupList()) {
                UserTask userTask = UserTask.builder()
                        .user(userGroup.getUser())
                        .task(task)
                        .isDone(false)
                        .build();
                task.addUserTask(userTask);
            }
            task.changeTaskType("group");
        }
    }

    @Override
    //TODO: UserTaskList 내부 확인
    public void makeTaskDone(Long taskId, Boolean done) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException(
        "Could not find task with ID: " + taskId));

        task.setTotalIsDone(true);

        taskRepository.save(task);
    }


    @Override
    public GetTaskCountListforMonthSendDTO getTaskCountListforMonth(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        YearMonth yearMonth = YearMonth.of(year,month);
        LocalDateTime startTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = yearMonth.atEndOfMonth().atTime(23,59,59);
        List<Task> taskList = taskRepository.findTasksBetweenDates(startTime,endTime);

        Integer[] countList = new Integer[32];
        Arrays.fill(countList,0);

        for(Task t : taskList){
            int dayOfMonth = t.getEndTime().getDayOfMonth();
            countList[dayOfMonth]++;
        }


        return GetTaskCountListforMonthSendDTO.builder()
                .number_list(Arrays.asList(countList))
                .build();
    }

    @Override
    public List<TaskListForDaySendDTO> getTaskListForDay(User user, LocalDate date) {
        List<Task> taskList = taskRepository.findAllByUserAndEndTime(user.getId(),date);

        List<TaskListForDaySendDTO> taskListForDaySendDTOList = new ArrayList<>();

        for(Task t : taskList){
            TaskListForDaySendDTO taskListForDaySendDTO = TaskListForDaySendDTO.builder()
                    .taskId(t.getId())
                    .taskName(t.getTaskName())
                    .tagId(t.getTag().getId())
                    .tagName(t.getTag().getTagName())
                    .endDate(t.getEndTime().toLocalDate())
                    .build();

            taskListForDaySendDTOList.add(taskListForDaySendDTO);
        }

        return taskListForDaySendDTOList;
    }

    @Override
    public ReadOneTaskSendDTO readOneTask(Long taskId) { //TODO: learn about Builders, and how to exclude / include fields.


        /**
         * RULES
         * 1. Personal task
         *      - only ONE USER in user list
         *      - the user's is_done must match the task's is done count
         *      - total_user_count must be one
         * 2. Group task
         *      - All of the Group must be in the user list
         *      - the number of user's is_done must match the task's is done count
         *      - total_user_count must be the size of the list.
         */
        Task task = taskRepository.findTaskByIdWithUserTaskList(taskId).orElseThrow(() -> new EntityNotFoundException(
                "Could not find group with ID: " + taskId +
                " in method readOneTask. Details: " + taskId));


        //convert task into readOneTaskSedDTO
        ReadOneTaskSendDTO readOneTaskSendDTO = ReadOneTaskSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .task_type(task.getTaskType())
                .group_name(task.getGroup().getGroupName())
                .tag_name(task.getTag().getTagName())
                .start_time(task.getStartTime().toLocalDate())
                .end_time(task.getEndTime().toLocalDate())
                .build();

//        ReadOneTaskSendDTO readOneTaskSendDTO= new ReadOneTaskSendDTO();
//        readOneTaskSendDTO.setTask_id(task.getId());
//        readOneTaskSendDTO.setTag_name(task.getTag().getTagName());
//        readOneTaskSendDTO.setTask_name(task.getTaskName());
//        readOneTaskSendDTO.setTask_type(task.getTaskType());
//        readOneTaskSendDTO.setGroup_name(task.getGroup().getGroupName());
//        readOneTaskSendDTO.setStart_time(task.getStartTime().toLocalDate());
//        readOneTaskSendDTO.setEnd_time(task.getEndTime().toLocalDate());

        fillUserListForReadOneTaskSendDTO(readOneTaskSendDTO,task);

        Integer total_user_count = 0;
        Integer is_done_count = 0;
        for(ReadOneTaskUserDTO userDTO : readOneTaskSendDTO.getUser_list()){
            total_user_count++;
            if(userDTO.getIs_done()){
                is_done_count++;
            }
        }
        readOneTaskSendDTO.setTotal_user_count(total_user_count);
        readOneTaskSendDTO.setIs_done_count(is_done_count);

        return readOneTaskSendDTO;
    }


    //TODO: 내가 다 해도 group원이 일을 끝내지 않았다면 표시합니까? --> 일단은 표시 안 하도록 하겠습니다.
    @Override
    public List<ReadTaskListByUserSendDTO> getTaskListForUser(User loginUser, Long last_task_id) {
        List<Task> taskList = taskRepository.findTasksByUserIdFetchUserTask(loginUser.getId());
        if(taskList.isEmpty()){
            throw new EntityNotFoundException(
                    "Could not get task list for user with ID (List came back empty): " + loginUser.getId() +
                    " in method getTaskListForUser ");
        }
        List<ReadTaskListByUserSendDTO> readTaskListByUserSendDTOList = new ArrayList<>();

        int taskListsize = taskList.size();
        int startIndex = 0;

        if(last_task_id != null){
            for(int i=0;i<taskListsize;i++){
                if(taskList.get(i).getId().equals(last_task_id)){
                    startIndex = i+1;
                    break;
                }
            }
        }

        if(startIndex >= taskListsize){ // no more data
            //TODO: what data to return when there is no more data?
            return null;
        }

        for(int i=startIndex;i<Math.min(startIndex+20,taskListsize);i++){
            Task task = taskList.get(i);

            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = ReadTaskListByUserSendDTO.builder()
                    .task_id(task.getId())
                    .task_name(task.getTaskName())
                    .start_time(task.getStartTime())
                    .end_time(task.getEndTime())
                    .task_type(task.getTaskType())
                    .tag_id(task.getTag().getId())
                    .tag_name(task.getTag().getTagName())
                    .group_id(task.getGroup().getId())
                    .group_color(task.getGroup().getGroupName())
                    .is_done_personal(false)
                    .is_done_total(false)
                    .build();


//            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = new ReadTaskListByUserSendDTO();
//            readTaskListByUserSendDTO.setTask_name(task.getTaskName());
//            readTaskListByUserSendDTO.setTask_id(task.getId());
//            readTaskListByUserSendDTO.setTask_type(task.getTaskType());
//            readTaskListByUserSendDTO.setStart_time(task.getStartTime());
//            readTaskListByUserSendDTO.setEnd_time(task.getEndTime());
//            readTaskListByUserSendDTO.setTag_id(task.getTag().getId());
//            readTaskListByUserSendDTO.setGroup_color(task.getGroup().getGroupName()); //TODO: Add group color to the group database
//            readTaskListByUserSendDTO.setIs_done_total(false);
//            readTaskListByUserSendDTO.setIs_done_personal(false);
        }

        return readTaskListByUserSendDTOList;
    }

    @Override
    public Collection<Task> readTaskListByGroup(
            Long lastId,
            long groupId,
            User user,
            boolean isMyTask,
            String completeStatus,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean withDueDate,
            List<Long> tagList
    ) {
        // prepare for query
        Boolean totalIsDone = false;
        if (!isMyTask) {
            user = null; // null means don't specify user
        }

        if (Objects.equals(completeStatus, "complete")) {
            totalIsDone = true;
        } else if (Objects.equals(completeStatus, "incomplete")) {
            totalIsDone = false;
        } else if (Objects.equals(completeStatus, "all")) {
            totalIsDone = null;
        }

        Task lastTask = null;
        LocalDateTime lastEndTime = null;
        if (lastId != null) {
            lastTask = taskRepository.findById(lastId).orElseThrow(
                    () -> new NoSuchElementException("Couldn't find task using lastId")
            );
            lastEndTime = lastTask.getEndTime();
        }
        var pageable = PageRequest.of(0, 20);
        return taskRepository.findAllByGroupWithArguments(
                lastId,
                lastEndTime,
                groupId,
                user,
                totalIsDone,
                startDate,
                endDate,
                tagList,
                pageable
        );
    }


    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public boolean getIsDoneMe(User user, Task task) {
        // loop through user task list to see if I exist
        for (var userTask : task.getUserTaskList()) {
            // If I exist, return if I am done
            if(Objects.equals(userTask.getUser(), user)) {
                return userTask.getIsDone();
            }
        }
        // I don't exist
        return false;
    }

    @Override
    public int getDoneUserCount(Task task) {
        int sum = 0;
        for (var userTask : task.getUserTaskList()) {
            if (userTask.getIsDone()) sum++;
        }
        return sum;
    }

    @Override
    public String getDoingNickname(Task task) {
        // Doesn't have dedicated user of task
        if (task.getUserTaskList().size() != 1) {
            return null;
        }

        return task.getUserTaskList().get(0).getUser().getNickname();
    }


    //friend functions.
    private static void fillUserListForReadOneTaskSendDTO(ReadOneTaskSendDTO readOneTaskSendDTO, Task task){

        List<ReadOneTaskUserDTO> userDTOList = new ArrayList<>();

        for(UserTask userTask : task.getUserTaskList()){
            ReadOneTaskUserDTO userDTO = ReadOneTaskUserDTO.builder()
                    .user_id(userTask.getUser().getId())
                    .nickname(userTask.getUser().getNickname())
                    .profile_image_id(userTask.getUser().getProfileImageId())
                    .is_done(userTask.getIsDone())
                    .build();

//            ReadOneTaskUserDTO userDTO = new ReadOneTaskUserDTO();
//            userDTO.setUser_id(userTask.getUser().getId());
//            userDTO.setNickname(userTask.getUser().getNickname());
//            userDTO.setProfile_image_id(userTask.getUser().getProfileImageId());
//            userDTO.setIs_done(userTask.getIsDone());

            userDTOList.add(userDTO);
        }
        readOneTaskSendDTO.setUser_list(userDTOList);
    }

}
