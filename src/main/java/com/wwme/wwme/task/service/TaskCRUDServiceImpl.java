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


    @Override
    public Task createTask(CreateTaskReceiveDTO createTaskReceiveDTO) {
        Task task = new Task();
        task.setTaskName(createTaskReceiveDTO.getTask_name());
        task.setStartTime(LocalDateTime.now());
        task.setTaskType(createTaskReceiveDTO.getTask_type());

        //set end time
        if(createTaskReceiveDTO.getEnd_time() != null){
            task.setEndTime(createTaskReceiveDTO.getEnd_time());
        }else{
            task.setEndTime(LocalDateTime.MAX);
        }


        if(createTaskReceiveDTO.getTag_id() != null){
            Tag tag = tagRepository.findById(createTaskReceiveDTO.getTag_id()).orElseThrow(() -> new EntityNotFoundException(
                    "Could not find tag with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                            " in method createTask. Details: " + createTaskReceiveDTO.toString()));
            task.setTag(tag);
        }




        Group group = groupRepository.findById(createTaskReceiveDTO.getGroup_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find group with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                " in method createTask. Details: " + createTaskReceiveDTO.toString()));
        task.setGroup(group);

        if(createTaskReceiveDTO.getTask_type().equals("personal")){
            User user = userRepository.findById(createTaskReceiveDTO.getTodo_user_id()).orElseThrow(() -> new EntityNotFoundException(
                    "Could not find user with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                            " in method createTask. Details: " + createTaskReceiveDTO.toString()));

            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTask(task);
            userTask.setIsDone(false);
            task.getUserTaskList().add(userTask);
        }else{ // add everyone in the group to the usertask list
            List<User> userList = userRepository.findAllByGroupID(createTaskReceiveDTO.getGroup_id());
            for (User u : userList){
                UserTask userTask = new UserTask();
                userTask.setUser(u);
                userTask.setTask(task);
                userTask.setIsDone(false);
                task.getUserTaskList().add(userTask);
            }
        }

        return taskRepository.save(task);

    }

    @Override
    //1. is done을 바꿀 수 없어야함.
    //2. 그렇다면 is done 을 어떻게 처리할 것인가?
    public Task updateTask(UpdateTaskReceiveDTO updateTaskReceiveDTO) {

        log.info(updateTaskReceiveDTO.toString());

        Task task = taskRepository.findTaskByIdWithUserTaskList(updateTaskReceiveDTO.getTask_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find task with ID: " + updateTaskReceiveDTO.getTask_id() +
                " in method updateTask. Details: " + updateTaskReceiveDTO.toString()));

        task.setTaskName(updateTaskReceiveDTO.getTask_name());
        task.setEndTime(updateTaskReceiveDTO.getEnd_time());
        task.setTaskType(updateTaskReceiveDTO.getTask_type());

        if(updateTaskReceiveDTO.getTag_id() != null){
            Tag tag = tagRepository.findById(updateTaskReceiveDTO.getTag_id()).orElseThrow(() -> new EntityNotFoundException(
                    "Could not find Tag with ID: " + updateTaskReceiveDTO.getTag_id() +
                            "in method updateTag. Details: " + updateTaskReceiveDTO.toString()));

            if(!tag.getGroup().getId().equals(updateTaskReceiveDTO.getGroup_id())){
                throw new EntityNotFoundException(
                        String.format("The tag ID [%d] you wish to update to is NOT in the current Group of the task[%d]" +
                                        "Current Task/Tag Group ID : %d" +
                                        "New Tag Group ID : %d "
                                ,tag.getId(),task.getId(),updateTaskReceiveDTO.getGroup_id(),tag.getGroup().getId())
                );
            }
            task.setTag(tag);
        }



        List<UserTask> userTaskList = task.getUserTaskList();
        task.setTotalIsDone(updateTaskReceiveDTO.getIs_done_total());

        return  taskRepository.save(task);
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
            endTime = LocalDateTime.MIN;
        }


        Pageable pageable = PageRequest.of(0,20);
        List<Task> taskList =  taskRepository.findTasksByUserIdFetchUserTask(loginUser.getId(), endTime, pageable);
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

        // only display tasks without due date
        if (!withDueDate) {
            startDate = LocalDateTime.MAX;
            endDate = LocalDateTime.MAX;
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
