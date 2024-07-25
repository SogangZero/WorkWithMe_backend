package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.notification.service.NotificationSender;
import com.wwme.wwme.log.domain.DTO.*;
import com.wwme.wwme.log.domain.OperationType;
import com.wwme.wwme.log.service.EventService;
import com.wwme.wwme.task.domain.DTO.receiveDTO.MakeTaskDoneReceiveDTO;
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
    private final NotificationSender notificationSender;
    private final EventService eventService;


    @Override
    public Task createTask(String taskName,
                           LocalDateTime endTime,
                           String taskType,
                           Long tagId,
                           Long groupId,
                           Long todoUserId,
                           User loginUser) throws IllegalArgumentException{

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

        checkUsersInSameGroup(taskType, loginUser, group, todoUser);

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
        Task newTask = taskRepository.save(taskEntity);

        //log 추가 로직 (after creating task)
        CreateTaskLogDTO createTaskLogDTO = CreateTaskLogDTO.buildWithSpecificParamsNoID()
                .task(newTask)
                .newTaskName(newTask.getTaskName())
                .user(loginUser)
                .operationTime(LocalDateTime.now())
                .operationTypeEnum(OperationType.CREATE_TASK)
                .group(group)
                .build();

        eventService.createEvent(createTaskLogDTO);


        return newTask;
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
        return tagRepository.findById(tagId).orElseThrow(() -> (
                new IllegalArgumentException("Could not find Tag of ID: "+tagId
                +" in function getTagFromDB")));
    }

    private static void checkParameterValidity(String taskName, LocalDateTime endTime, String taskType) {
        LocalDateTime checkEndTime = LocalDateTime.of(endTime.getYear(),endTime.getMonthValue(),endTime.getDayOfMonth(),23,59,59);

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
                           String taskName,
                           LocalDateTime endTime,
                           String taskType,
                           Long tagId,
                           Long todoUserId,
                           User loginUser) {
        Task task = getTaskFromDB(taskId);
        Tag tag = (tagId == null) ? null : getTagFromDB(tagId);
        validateLoginUser(loginUser, task);

        User todoUser = null;
        if(Objects.equals(taskType, "personal")){
            if(todoUserId == null){
                throw new IllegalArgumentException("The todoUser ID is null when updating to a personal task ID :"
                + taskId + " in function updateTask");
            }
            validateTodoUser(todoUserId,task);
            todoUser = getTodoUserFromDB(todoUserId);
            updateTodoUser(task, todoUser);
        }

        List<EventDTO> eventDTOList = new ArrayList<>();

        var beforeUsers = getUsersFromTask(task);

        //업데이트 + 로그
        eventDTOList.add(updateTaskName(taskName,task,loginUser));
        eventDTOList.add(updateTag(tag,task,loginUser));
        eventDTOList.add(updateEndTime(endTime, task, loginUser));
        eventDTOList.add(updateTaskType(taskType, task, todoUser, loginUser));


        var afterUsers = getUsersFromTask(task);
        Set<User> notifyUsers = new HashSet<>();
        beforeUsers.forEach(curUser -> {
            if (!notifyUsers.contains(curUser)) notifyUsers.add(curUser);
        });
        afterUsers.forEach(curUser -> {
            if (!notifyUsers.contains(curUser)) notifyUsers.add(curUser);
        });

        notificationSender.sendOnMyTaskChange(task, notifyUsers, loginUser);


        eventDTOList.stream()
                .filter(Objects::nonNull)
                .forEach(eventService::createEvent);

        return task;
    }

    private UpdateTaskNameLogDTO updateTaskName(String taskName, Task task,User loginUser) {


        if(!taskName.equals(task.getTaskName())){
            UpdateTaskNameLogDTO updateTaskNameLogDTO = UpdateTaskNameLogDTO.buildWithSpecificParamsNoID()
                    .operationTypeEnum(OperationType.UPDATE_TASK_NAME)
                    .task(task)
                    .user(loginUser)
                    .operationTime(LocalDateTime.now())
                    .beforeTaskName(task.getTaskName())
                    .afterTaskName(taskName)
                    .group(task.getGroup())
                    .build();
            task.setTaskName(taskName);
            return updateTaskNameLogDTO;
        }else{
            return null;
        }


    }

    private Collection<User> getUsersFromTask(Task task) {
        return task.getUserTaskList().stream().map(UserTask::getUser).toList();
    }

    private void updateIsDoneTotal(Task task,User todoUser) {

        if(task.getUserTaskList().isEmpty()){
            throw new IllegalArgumentException("UserTaskList inside Task is empty in function updateIsDoneTotal");
        }


        switch (task.getTaskType()){
            case "anyone":
                for(UserTask ut : task.getUserTaskList()){
                    if (ut.getIsDone()) {
                        task.setTotalIsDone(true);
                        return;
                    }
                }
                task.setTotalIsDone(false);
                break;
            case "personal":
                for(UserTask ut : task.getUserTaskList()){
                    if (ut.getUser().getId().equals(todoUser.getId())) {
                        task.setTotalIsDone(ut.getIsDone());
                        break;
                    }
                }
                break;
            case "group":
                for(UserTask ut : task.getUserTaskList()){
                    if (!ut.getIsDone()) {
                        task.setTotalIsDone(false);
                        return;
                    }
                }
                task.setTotalIsDone(true);
                break;
            default:
                throw new NoSuchElementException("Illegal task type : "+task.getTaskType()
                +"In function updateIsDoneTotal");
        }
    }

    private static void validateLoginUser(User loginUser, Task task) {
        if (!task.getGroup().existUser(loginUser)) {
            throw new IllegalArgumentException("The task's group of NAME : " + task.getGroup().getGroupName()
                    +"ID : " +task.getGroup().getId() +"did not match the group of loginUser of ID: "+loginUser
            +" in function validateUsers");
        }
    }

    private static void validateTodoUser(Long todoUserId, Task task) {
        if (!task.getGroup().existUserById(todoUserId)) {
            throw new IllegalArgumentException("The task's group of NAME : " + task.getGroup().getGroupName()
                    +"ID : " +task.getGroup().getId() +"did not match the group of todoUser of ID: "+todoUserId
                    +" in function validateTodoUser");
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

    private UpdateTaskTypeLogDTO updateTaskType(String taskType, Task task, User todoUser, User loginUser) {

        String originTaskType = task.getTaskType();
        log.info("original task type : "+task.getTaskType());
        log.info("changing task type : "+taskType);
        if (taskType != null && !task.getTaskType().equals(taskType)) { //changes taskType
            changeTaskType(taskType, task, todoUser);

            return UpdateTaskTypeLogDTO.buildWithSpecificParamsNoID()
                    .operationTypeEnum(OperationType.UPDATE_TASK_TYPE)
                    .operationTime(LocalDateTime.now())
                    .task(task)
                    .user(loginUser)
                    .beforeTaskType(originTaskType)
                    .afterTaskType(taskType)
                    .group(task.getGroup())
                    .build();
        }else{
            return null;
        }
    }

    private static UpdateTaskDueDateLogDTO updateEndTime(LocalDateTime endTime, Task task, User loginUser) {
        if (endTime != null && !task.getEndTime().equals(endTime)) {
            LocalDateTime originEndTime = task.getEndTime();
            if (endTime.isBefore(setToStartOfDay(task.getStartTime()))) {
                log.info(endTime.toString());
                log.info(setToStartOfDay(task.getStartTime()).toString());

                throw new IllegalArgumentException("Update Task Fail - The Changed Endtime is before startTime in function updateEndTime");
            }
            task.changeEndTime(endTime);

            return UpdateTaskDueDateLogDTO.buildWithSpecificParamsNoID()
                    .operationTypeEnum(OperationType.UPDATE_TASK_DUE_DATE)
                    .operationTime(LocalDateTime.now())
                    .task(task)
                    .user(loginUser)
                    .group(task.getGroup())
                    .previousDueDate(originEndTime)
                    .updatedDueDate(endTime)
                    .build();
        }else{
            return null;
        }
    }

    private static LocalDateTime setToEndOfDay(LocalDateTime localDateTime){
        return LocalDateTime.of(localDateTime.getYear(),localDateTime.getMonthValue(),localDateTime.getDayOfMonth(),
                11,59,59);
    }

    private static LocalDateTime setToStartOfDay(LocalDateTime localDateTime){
        return LocalDateTime.of(localDateTime.getYear(),localDateTime.getMonthValue(),localDateTime.getDayOfMonth(),
                0,0,0);
    }

    private EventDTO updateTag(Tag tag, Task task, User loginUser) {
        if(isTagUpdateScenario(task, tag)){
            Tag prevTag = task.getTag();
            String prevTagName = (prevTag != null) ? prevTag.getTagName() : "해당 없음";

            if (!task.validateTagInGroup(tag)) {
                throw new IllegalArgumentException("Update Task Fail - Tag Not Matched Group");
            }
            task.changeTag(tag);

            //Logging feature
            if(tag != null){
                return UpdateTaskChangeTagDTO.buildWithSpecificParamsNoID()
                        .task(task)
                        .user(loginUser)
                        .group(task.getGroup())
                        .operationTypeEnum(OperationType.UPDATE_TASK_CHANGE_TAG)
                        .operationTime(LocalDateTime.now())
                        .updateTagName(tag.getTagName())
                        .previousTagName(prevTagName)
                        .build();
            }else{//Tag is null --> update task delete tag
                return UpdateTaskDeleteTagDTO.buildWithSpecificParamsNoID()
                        .task(task)
                        .user(loginUser)
                        .group(task.getGroup())
                        .operationTypeEnum(OperationType.UPDATE_TASK_DELETE_TAG)
                        .operationTime(LocalDateTime.now())
                        .deletedTag(prevTag)
                        .build();
            }

        }else{
            return null;
        }

    }
    private boolean isTagUpdateScenario(Task task, Tag tag){
        if(task.getTag() == null){
            return tag != null;
        }else{
            return task.getTag().equals(tag);
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
            log.info("User In UserTask : "+ut.getUser().getNickname());
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
                log.info("UserTask Info : User["+ut.getUser().getNickname()+"]" +
                        " Task ["+ ut.getTask().getId() +"]" + " IsDone :  "+ut.getIsDone());
                if(ut.getIsDone()){
                    log.info("User  ["+ut.getUser().getNickname() + "] has finished "+
                            " anyone task ["+task.getTaskName() + "], will break");
                    isDoneFlag = true;
                    is_done_count++;
                    break;
                }
            }
            total_user_count = task.getUserTaskList().size();
            log.info("total is done flag : "+isDoneFlag);
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
            //fill in tag
            ROT_tagDTO rotTagDTO = null;
            if(t.getTag() != null){
                rotTagDTO = ROT_tagDTO.builder()
                        .tag_id(t.getTag().getId())
                        .tag_name(t.getTag().getTagName())
                        .build();
            }


            TaskListForDaySendDTO taskListForDaySendDTO = TaskListForDaySendDTO.builder()
                    .taskId(t.getId())
                    .taskName(t.getTaskName())
                    .tag(rotTagDTO)
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

        //fill in tag
        ROT_tagDTO rotTagDTO = null;
        if(task.getTag() != null){
            rotTagDTO = ROT_tagDTO.builder()
                    .tag_id(task.getTag().getId())
                    .tag_name(task.getTag().getTagName())
                    .build();
        }



        //convert task into readOneTaskSedDTO
        ReadOneTaskSendDTO readOneTaskSendDTO = ReadOneTaskSendDTO.builder()
                .task_id(task.getId())
                .task_name(task.getTaskName())
                .task_type(task.getTaskType())
                .tag(rotTagDTO)
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

        for(ReadOneTaskUserDTO userDTO : readOneTaskSendDTO.getUser_list()){
            if(userDTO.getIs_done()){
                is_done_count++;
            }
            if(userDTO.getUser_id().equals(loginUser.getId()) && userDTO.getIs_done()){
                is_done_personal = true;
            }
        }
        readOneTaskSendDTO.setIs_done_count(is_done_count);
        readOneTaskSendDTO.setIs_done_total(task.getTotalIsDone());
        readOneTaskSendDTO.setIs_done_personal(is_done_personal);
        readOneTaskSendDTO.setIs_mine(isMyTask(task, loginUser));




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

            ROT_tagDTO return_rotTagDTO = null;
            //insert tag information
            if(t.getTag() != null){
                return_rotTagDTO = ROT_tagDTO.builder()
                        .tag_id(t.getTag().getId())
                        .tag_name(t.getTag().getTagName())
                        .build();
            }



            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = ReadTaskListByUserSendDTO.builder()
                    .task_id(t.getId())
                    .task_name(t.getTaskName())
                    .task_type(t.getTaskType())
                    .start_time(t.getStartTime())
                    .end_time(t.getEndTime())
                    .tag(return_rotTagDTO)
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

        // only display tasks with due date
        if (!withDueDate) {
            endDate = LocalDateTime.of(2099,12,12,12,12,11);
        }

        // contain all tags
        boolean allTags = tagList.isEmpty();

        // tag list has -1 -> give tasks  that doesn't have tag
        boolean containNoTagTask = false;
        if (tagList.contains(-1L)) {
            tagList.remove(-1L);
            containNoTagTask = true;
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
                allTags,
                containNoTagTask,
                pageable
        );
    }


    @Override
    public void deleteTask(Long taskId, User loginUser) {

        Task task = taskRepository.findById(taskId).orElseThrow(()-> new NoSuchElementException("Could" +
                "not find task with ID : "+ taskId + "in function deleteTask"));

        log.info("Task With Id ["+taskId+"] exists in DB");

        //create log DTO
        DeleteTaskLogDTO deleteTaskLogDTO = DeleteTaskLogDTO
                .buildWithSpecificParams()
                .task(null)
                .group(task.getGroup())
                .user(loginUser)
                .operationTime(LocalDateTime.now())
                .operationTypeEnum(OperationType.DELETE_TASK)
                .deletedTaskName(task.getTaskName())
                .build();


        taskRepository.deleteById(taskId);
        eventService.createEvent(deleteTaskLogDTO);
    }

    @Override
    public Task getTaskByID(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                ()->new NoSuchElementException("Could not find Task of ID : "+taskId +"in " +
                        "function getTaskByID")
        );

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
    public int getTotalUserCount(Task task) {
        return task.getUserTaskList().size();
    }

    @Override
    public String getDoingNickname(Task task) {
        // Doesn't have dedicated user of task
        if (task.getUserTaskList().size() != 1) {
            return null;
        }

        return task.getUserTaskList().get(0).getUser().getNickname();
    }

    @Override
    public Collection<UserTask> findAllTodayDueDateTasks() {
        var now = LocalDate.now();
        return userTaskRepository.findAllByEndTime(now);
    }

    @Override
    public boolean isMyTask(Task task, User user) {
        for (UserTask userTask : task.getUserTaskList()) {
            boolean taskIsMine = userTask.getUser().equals(user);
            if (taskIsMine) {
                return true;
            }
        }
        return false;
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
