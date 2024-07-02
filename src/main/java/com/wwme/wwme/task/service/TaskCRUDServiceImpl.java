package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.MakeTaskDoneReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.task.repository.UserTaskRepository;
import com.wwme.wwme.user.domain.dto.MakeTaskDoneSendUserDTO;
import com.wwme.wwme.user.domain.dto.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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

        if(endTime == null){
            endTime = LocalDateTime.of(2099,12,12,12,12,12);
        }

        //parameter validation
        checkParameterValidity(taskName, endTime, taskType);
        Group group = getGroupFromDB(groupId);
        User todoUser = getTodoUserForPersonalTask(taskType, todoUserId);

        Tag tag = null;
        log.info("Tag Id =  "+tagId);
        if(tagId != null){
            log.info("Tag ID IS NOT NULL! Tag_ID : "+tagId);
            tag = getTagFromDB(tagId);
            checkGroupIncludeTag(tag, group);
        }

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
        log.info("Before return to Controller : Task Create");
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
        if (endTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Create Task Fail - EndTime is before now");
        }
        if (taskType == null || !(taskType.equals("group") || taskType.equals("personal") || taskType.equals("anyone"))) {
            throw new IllegalArgumentException("Create Task Fail - No matched Task Type");
        }

    }

    private void checkGroupIncludeTag(Tag tag, Group group) {
        if (tag == null) {
            return; // If tag is null, no further checks are needed
        }

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
            case "group", "anyone" -> {
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

            default -> throw new IllegalArgumentException("Create Task Fail - No Matched Task Type");
        }
    }

    @Override
    //TODO: update 한 뒤에 Task 자체의 is_done 을 확인해줘야 한다.
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
        log.info("original task type : "+task.getTaskType());
        log.info("changing task type : "+taskType);
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
        //personal -> anyone : 그룹원 모두 UserTask 객체 할당
        //TODO : fix personal -> anyone : UserTask needed for all group members
        if (task.getTaskType().equals("personal") && taskType.equals("anyone")) {
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
            task.changeTaskType("anyone");
        }

        //group -> personal : group원들의 UserTask를 todoUser를 제외하고 삭제
        if (task.getTaskType().equals("group") && taskType.equals("personal")) {
            userTaskRepository.deleteByTaskExceptForOnePerson(task, todoUser);
            task.changeTaskType("personal");
        }
        //group -> anyone : 그대로 놔둠
        if (task.getTaskType().equals("group") && taskType.equals("anyone")) {
            task.changeTaskType("anyone");
        }

        //anyone -> personal : personal에 대한 userTask를 추가해줌
        //TODO: userTask 를 먼저 모두 지워줘야함.
        if (task.getTaskType().equals("anyone") && taskType.equals("personal")) {
            userTaskRepository.deleteByTaskExceptForOnePerson(task, todoUser);
            task.changeTaskType("personal");
        }

        //anyone -> group : group원들의 UserTask를 추가해줌
        //TODO: 따로 추가가 필요하지 않다.
        if (task.getTaskType().equals("anyone") && taskType.equals("group")) {
            task.changeTaskType("group");
        }
    }

    @Override
    public MakeTaskDoneSendDTO makeTaskDone(MakeTaskDoneReceiveDTO makeTaskDoneReceiveDTO, User user) {
        Task task = taskRepository.findTaskByIdWithUserTaskList(makeTaskDoneReceiveDTO.getTask_id()).orElseThrow(() -> new EntityNotFoundException(
        "Could not find task with ID: " + makeTaskDoneReceiveDTO.getTask_id()));

        //setting the task's isdone for the user
        for (UserTask ut : task.getUserTaskList()){
            log.info("user : "+ut.getUser().getNickname());
            if(ut.getUser().getId().equals(user.getId())){
                ut.setIsDone(makeTaskDoneReceiveDTO.getDone());
            }
        }

        //Checking if the task is truly complete or not

        //plus set variables is_done count and total_user_count
        int is_done_count = 0;
        int total_user_count = 0;

        if (task.getTaskType().equals("group")){
            boolean isDoneFlag = true;
            for(UserTask ut : task.getUserTaskList()){
                if(!ut.getIsDone()){
                    isDoneFlag = false;
                }else{
                    is_done_count++;
                }
            }
            total_user_count = task.getUserTaskList().size();
            task.setTotalIsDone(isDoneFlag);
        }else if (task.getTaskType().equals("personal")){
            task.setTotalIsDone(makeTaskDoneReceiveDTO.getDone());
            total_user_count = 1;
            if(makeTaskDoneReceiveDTO.getDone()){
                is_done_count = 1;
            }
        }else{ // anyone task
            boolean isDoneFlag = false;
            for(UserTask ut : task.getUserTaskList()){
                if(ut.getIsDone()){
                    isDoneFlag = true;
                    is_done_count = 1;
                    break;
                }
            }
            total_user_count = task.getUserTaskList().size();
            task.setTotalIsDone(isDoneFlag);
        }

        taskRepository.save(task);

        List<MakeTaskDoneSendUserDTO> makeTaskDoneSendUserDTOList = new ArrayList<>();
        for(UserTask ut : task.getUserTaskList()){
            MakeTaskDoneSendUserDTO makeTaskDoneSendUserDTO = MakeTaskDoneSendUserDTO.builder()
                    .user_id(ut.getUser().getId())
                    .nickname(ut.getUser().getNickname())
                    .profile_image_id(ut.getUser().getProfileImageId())
                    .is_done(ut.getIsDone())
                    .build();
            makeTaskDoneSendUserDTOList.add(makeTaskDoneSendUserDTO);
        }

        log.info("is done count : :"+ is_done_count);
        log.info("total user count : "+total_user_count);

        return MakeTaskDoneSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .is_done_count((long) is_done_count)
                .total_user_count((long) total_user_count)
                .user_list(makeTaskDoneSendUserDTOList)
                .task_type(task.getTaskType())
                .group_name(task.getGroup().getGroupName())
                .start_time(task.getStartTime())
                .end_time(task.getEndTime())
                .build();


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
        List<Task> taskList = taskRepository.findAllByUserAndStartEndTimes(user.getId(),date.atStartOfDay(),date.atTime(LocalTime.MAX));
        log.info("date : "+date);
        log.info("user : "+user.toString());
        List<TaskListForDaySendDTO> taskListForDaySendDTOList = new ArrayList<>();

        for(Task t : taskList){
            TaskListForDaySendDTO taskListForDaySendDTO = TaskListForDaySendDTO.builder()
                    .taskId(t.getId())
                    .taskName(t.getTaskName())
                    .tag(new ROT_tagDTO(
                            Optional.ofNullable(t.getTag()).map(Tag::getId).orElse(null),
                            Optional.ofNullable(t.getTag()).map(Tag::getTagName).orElse(null)
                    ))
                    .endDate(t.getEndTime().toLocalDate())
                    .build();

            taskListForDaySendDTOList.add(taskListForDaySendDTO);
        }

        return taskListForDaySendDTOList;
    }

    @Override
    public ReadOneTaskSendDTO readOneTask(Long taskId, User loginUser) {

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
                "Could not find task with ID: " + taskId +
                " in method readOneTask. Details: " + taskId));

        Group group = groupRepository.findGroupByIdLoadUserTaskList(task.getGroup().getId()).orElseThrow(()-> new EntityNotFoundException(
                "Could not find group with ID: " + task.getGroup().getId() +
                        " in method readOneTask. Details: " + task.getGroup().getId()
        ));




        //convert task into readOneTaskSedDTO
        ReadOneTaskSendDTO readOneTaskSendDTO = ReadOneTaskSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .task_type(task.getTaskType())
                .tag(new ROT_tagDTO(
                        Optional.ofNullable(task.getTag()).map(Tag::getId).orElse(null),
                        Optional.ofNullable(task.getTag()).map(Tag::getTagName).orElse(null)
                        ))
                .group(new ROT_groupDTO(
                        task.getGroup().getId(),
                        task.getGroup().getGroupName(),
                        group.getUserGroupList().size(),
                        group.getUserGroupList().stream()
                        .filter(s -> s.getUser().getId().equals(loginUser.getId()))
                        .findAny()
                        .orElseThrow(() -> new NoSuchElementException("Could not find UserGroup Matching" +
                                "user id " + loginUser.getId() + "in function fillUserListForReadOneTaskSendDTO"))
                                .getColor()

                ))
                .start_time(task.getStartTime())
                .end_time(task.getEndTime())
                .build();

        fillUserListForReadOneTaskSendDTO(readOneTaskSendDTO,task);

        //is_done_count, is_done_personal, is_done_total;

        Integer is_done_count = 0;
        boolean is_done_personal = false;
        boolean is_done_total = true;
        for(ReadOneTaskUserDTO userDTO : readOneTaskSendDTO.getUser_list()){
            if(userDTO.getIs_done()){
                is_done_count++;
            }else{
                is_done_total = false;
            }

            if(userDTO.getUser_id().equals(loginUser.getId()) && userDTO.getIs_done()){
                is_done_personal = true;
            }
        }
        readOneTaskSendDTO.setIs_done_count(is_done_count);
        readOneTaskSendDTO.setIs_done_total(is_done_total);
        readOneTaskSendDTO.setIs_done_personal(is_done_personal);




        return readOneTaskSendDTO;
    }


    @Override
    public List<ReadTaskListByUserSendDTO> getTaskListForUser(User loginUser, Long last_task_id) {
        log.info("loginUser : "+loginUser);
        log.info("last_task_id : "+last_task_id);

        LocalDateTime endTime;
        if(last_task_id != null){
             endTime =  taskRepository.findById(last_task_id).orElseThrow(
                    ()-> new NoSuchElementException("Could not find Task with ID: "+last_task_id
                            +"In function getTaskListForUser")).getEndTime();
        }else{
            endTime = LocalDateTime.of(1999,12,7,12,12,12);
        }


        Pageable pageable = PageRequest.of(0,20);
        List<Task> taskList =  taskRepository.findTasksByUserIdFetchUserTask(loginUser.getId(), endTime, last_task_id ,pageable);
        log.info("tasklist elements : "+ taskList.size());
        List<ReadTaskListByUserSendDTO> readTaskListByUserSendDTOList = new ArrayList<>();

        for(Task t : taskList){
            //is done personal 구하기
            boolean is_done_personal = false;
            for(UserTask ut : t.getUserTaskList()){
                if(ut.getUser().getId().equals(loginUser.getId())){
                    is_done_personal = ut.getIsDone();
                    break;
                }
            }
            //TODO: insert group color
            Group group = groupRepository.findGroupByIdLoadUserTaskList(t.getGroup().getId())
                    .orElseThrow(()->new NoSuchElementException("Could not find group with ID: "+ t.getGroup().getId()
                                                                + "in fuction getTaskListForUser"));
            log.info("found group : "+ group.getId());
            UserGroup ug = group.getUserGroupList().stream()
                    .filter(s -> s.getUser().getId().equals(loginUser.getId())).findAny()
                    .orElseThrow(()-> new NoSuchElementException("Could not find userGroup with userID: "
                            + loginUser.getId() + "In function getTaskListForUser" ));



            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = ReadTaskListByUserSendDTO.builder()
                    .task_id(t.getId())
                    .task_name(t.getTaskName())
                    .task_type(t.getTaskType())
                    .start_time(t.getStartTime())
                    .end_time(t.getEndTime())
                    .tag(new ROT_tagDTO(
                            Optional.ofNullable(t.getTag()).map(Tag::getId).orElse(null),
                            Optional.ofNullable(t.getTag()).map(Tag::getTagName).orElse(null)))
                    .group(new RTL_groupDTO(
                            t.getGroup().getId(),
                            ug.getColor(),
                            t.getGroup().getGroupName()
                    ))
                    .is_done_personal(is_done_personal)
                    .is_done_total(t.getTotalIsDone())
                    .build();

            readTaskListByUserSendDTOList.add(readTaskListByUserSendDTO);
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

        if (startDate == null)
            startDate = LocalDateTime.of(1999,12,12,12,12,12);
        if (endDate == null)
            endDate = LocalDateTime.of(2099,12,12,12,12,12);

        // only display tasks without due date
        if (!withDueDate) {
            startDate = LocalDateTime.of(2099,12,12,12,12,12);
            endDate = LocalDateTime.of(2099,12,12,12,12,12);
        }

        Task lastTask;
        LocalDateTime lastEndTime = null;
        if (lastId != null) {
            lastTask = taskRepository.findById(lastId).orElseThrow(
                    () -> new NoSuchElementException("Couldn't find task using lastId")
            );
            lastEndTime = lastTask.getEndTime();
        }
        var pageable = PageRequest.of(0, 20);
        log.info("{} {} {} {}", startDate, endDate, totalIsDone, groupId);
        return taskRepository.findAllByGroupWithArguments(
                lastId,
                lastEndTime,
                groupId,
                user,
                totalIsDone,
                startDate,
                endDate,
                tagList,
                tagList.size(),
                pageable
        );
    }


    @Override
    public void deleteTask(Long taskId) {
        if(taskRepository.findById(taskId).isEmpty()) {
            throw new NoSuchElementException("Could not find Task with ID: "+taskId
            +"in function deleteTask");
        }
        log.info("Task With Id ["+taskId+"] exists in DB");
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

            userDTOList.add(userDTO);
        }
        readOneTaskSendDTO.setUser_list(userDTOList);
    }

}
