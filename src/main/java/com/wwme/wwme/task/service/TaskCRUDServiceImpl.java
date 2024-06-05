package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.DTO.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        task.setStartTime(createTaskReceiveDTO.getStart_time());
        task.setEndTime(createTaskReceiveDTO.getEnd_time());
        task.setTaskType(createTaskReceiveDTO.getTask_type());

        User user = userRepository.findById(createTaskReceiveDTO.getTodo_user_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find user with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                " in method createTask. Details: " + createTaskReceiveDTO.toString()));

        Tag tag = tagRepository.findById(createTaskReceiveDTO.getTag_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find tag with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                " in method createTask. Details: " + createTaskReceiveDTO.toString()));

        task.setTag(tag);

        Group group = groupRepository.findById(createTaskReceiveDTO.getGroup_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find group with ID: " + createTaskReceiveDTO.getTodo_user_id() +
                " in method createTask. Details: " + createTaskReceiveDTO.toString()));
        task.setGroup(group);

        if(createTaskReceiveDTO.getTask_type().equals("personal")){
            UserTask userTask = new UserTask();
            userTask.setUser(user);
            userTask.setTask(task);

            task.getUserTaskList().add(userTask);
        }

        return taskRepository.save(task);

    }

    @Override
    //TODO: needs fixing (논의 필요)
    public Task updateTask(UpdateTaskReceiveDTO updateTaskReceiveDTO) {
        Task task = taskRepository.findById(updateTaskReceiveDTO.getTask_id()).orElseThrow(() -> new EntityNotFoundException(
                "Could not find task with ID: " + updateTaskReceiveDTO.getTask_id() +
                " in method updateTask. Details: " + updateTaskReceiveDTO.toString()));

        task.setTaskName(updateTaskReceiveDTO.getTask_name());
        task.setStartTime(updateTaskReceiveDTO.getStart_time());
        task.setEndTime(updateTaskReceiveDTO.getEnd_time());
        task.setTaskType(updateTaskReceiveDTO.getTask_type());

        //inefficient
        Tag tag = new Tag();
        tag.setId(updateTaskReceiveDTO.getTag_id());

        Group group = new Group();
        group.setId(updateTaskReceiveDTO.getGroup_id());

        task.setTag(tag);
        task.setGroup(group);

        task.setTotalIsDone(updateTaskReceiveDTO.getIs_done_total());



        return  taskRepository.save(task);
    }

    @Override
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

        for(Task t : taskList){
            int dayOfMonth = t.getEndTime().getDayOfMonth();
            countList[dayOfMonth]++;
        }

        GetTaskCountListforMonthSendDTO getTaskCountListforMonthSendDTO = new GetTaskCountListforMonthSendDTO();
        getTaskCountListforMonthSendDTO.setNumber_list(Arrays.asList(countList));
        return getTaskCountListforMonthSendDTO;
    }

    @Override
    public List<TaskListForDaySendDTO> getTaskListForDay(User user, LocalDate date) {
        List<Task> taskList = taskRepository.findAllByUserAndEndTime(user.getId(),date);

        List<TaskListForDaySendDTO> taskListForDaySendDTOList = new ArrayList<>();

        for(Task t : taskList){
            TaskListForDaySendDTO taskListForDaySendDTO = new TaskListForDaySendDTO();
            taskListForDaySendDTO.setTaskId(t.getId());
            taskListForDaySendDTO.setTaskName(t.getTaskName());
            taskListForDaySendDTO.setTagId(t.getTag().getId());
            taskListForDaySendDTO.setTagName(t.getTag().getTagName());
            taskListForDaySendDTO.setEndDate(t.getEndTime().toLocalDate());

            taskListForDaySendDTOList.add(taskListForDaySendDTO);
        }

        return taskListForDaySendDTOList;
    }

    @Override
    public ReadOneTaskSendDTO readOneTask(Long taskId) {
        ReadOneTaskSendDTO readOneTaskSendDTO= new ReadOneTaskSendDTO();

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
        readOneTaskSendDTO.setTask_id(task.getId());
        readOneTaskSendDTO.setTag_name(task.getTag().getTagName());
        readOneTaskSendDTO.setTask_name(task.getTaskName());
        readOneTaskSendDTO.setTask_type(task.getTaskType());
        readOneTaskSendDTO.setGroup_name(task.getGroup().getGroupName());
        readOneTaskSendDTO.setStart_time(task.getStartTime().toLocalDate());
        readOneTaskSendDTO.setEnd_time(task.getEndTime().toLocalDate());

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

            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = new ReadTaskListByUserSendDTO();
            readTaskListByUserSendDTO.setTask_name(task.getTaskName());
            readTaskListByUserSendDTO.setTask_id(task.getId());
            readTaskListByUserSendDTO.setTask_type(task.getTaskType());
            readTaskListByUserSendDTO.setStart_time(task.getStartTime());
            readTaskListByUserSendDTO.setEnd_time(task.getEndTime());
            readTaskListByUserSendDTO.setTag_id(task.getTag().getId());
            readTaskListByUserSendDTO.setGroup_color(task.getGroup().getGroupName()); //TODO: Add group color to the group database
            readTaskListByUserSendDTO.setIs_done_total(false);
            readTaskListByUserSendDTO.setIs_done_personal(false);
        }

        return readTaskListByUserSendDTOList;
    }

    @Override
    public List<TaskListReadByGroupSendDTO> readTaskListByGroup(TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO) {
        return null;
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }



    //friend functions.
    private static void fillUserListForReadOneTaskSendDTO(ReadOneTaskSendDTO readOneTaskSendDTO, Task task){

        List<ReadOneTaskUserDTO> userDTOList = new ArrayList<>();

        for(UserTask userTask : task.getUserTaskList()){
            ReadOneTaskUserDTO userDTO = new ReadOneTaskUserDTO();
            userDTO.setUser_id(userTask.getUser().getId());
            userDTO.setNickname(userTask.getUser().getNickname());
            userDTO.setProfile_image_id(userTask.getUser().getProfileImageId());
            userDTO.setIs_done(userTask.getIsDone());

            userDTOList.add(userDTO);
        }
        readOneTaskSendDTO.setUser_list(userDTOList);
    }

}
