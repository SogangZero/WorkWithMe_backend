package com.wwme.wwme.task.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.task.domain.DTO.receiveDTO.CreateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.TaskListReadByGroupReceiveDTO;
import com.wwme.wwme.task.domain.DTO.receiveDTO.UpdateTaskReceiveDTO;
import com.wwme.wwme.task.domain.DTO.sendDTO.*;
import com.wwme.wwme.task.domain.DTO.TaskDTO;
import com.wwme.wwme.task.domain.Tag;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.domain.UserTask;
import com.wwme.wwme.task.repository.TagRepository;
import com.wwme.wwme.task.repository.TaskRepository;
import com.wwme.wwme.user.domain.DTO.ReadOneTaskUserDTO;
import com.wwme.wwme.user.domain.User;
import com.wwme.wwme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.ast.NullLiteral;
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

        User user = userRepository.findById(createTaskReceiveDTO.getTodo_user_id()).orElseThrow();

        Tag tag = tagRepository.findById(createTaskReceiveDTO.getTag_id()).orElseThrow();
        task.setTag(tag);

        Group group = groupRepository.findById(createTaskReceiveDTO.getGroup_id()).orElseThrow();
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
    public Task updateTask(UpdateTaskReceiveDTO updateTaskReceiveDTO) {
        Task task = taskRepository.findById(updateTaskReceiveDTO.getTask_id()).orElseThrow();
        task.setTaskName(updateTaskReceiveDTO.getTask_name());
        task.setStartTime(updateTaskReceiveDTO.getStart_time());
        task.setEndTime(updateTaskReceiveDTO.getEnd_time());
        task.setTaskType(updateTaskReceiveDTO.getTask_type());

        //inefficient
        Tag tag = tagRepository.findById(updateTaskReceiveDTO.getTag_id()).orElseThrow();
        Group group = groupRepository.findById(updateTaskReceiveDTO.getGroup_id()).orElseThrow();

        task.setTag(tag);
        task.setGroup(group);

        //TODO: should this be  totalIsDone or MineIsDone(?)
        task.setTotalIsDone(updateTaskReceiveDTO.getIs_done());

        return  taskRepository.save(task);
    }

    @Override
    public void makeTaskDone(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
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
        List<Task> taskList = taskRepository.findAllByUserAndDate(user,date);

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
         * 1. Personal task
         *      total user count = 1;
         *      is done count = 0 / 1
         *      user list = 한 명의 정보
     *      2. group task
         *      total user count = group 구성원 수;
         *      is done count = real search and insert
         *      user_list = load all users from group
         *
         */
        Task task = taskRepository.findByIdWithUserTaskList(taskId).orElseThrow();
        String taskType = task.getTaskType();
        readOneTaskSendDTO.setTask_id(task.getId());
        readOneTaskSendDTO.setTask_name(task.getTaskName());
        readOneTaskSendDTO.setTask_type(taskType);
        readOneTaskSendDTO.setStart_time(task.getStartTime().toLocalDate());
        readOneTaskSendDTO.setEnd_time(task.getEndTime().toLocalDate());

        List<ReadOneTaskUserDTO> readOneTaskUserDTOList = new ArrayList<>();
        Integer totalUserCount = 0;
        Integer isDoneCount = 0;

        if(taskType.equals("group")){
            for (UserTask userTask : task.getUserTaskList()){
                //add readOneUserDTO to List.
                getReadOneTaskUserDTO(userTask);

                //add to total user count
                totalUserCount++;
                if(userTask.getIsDone()){
                    //add to is_done_count
                    isDoneCount++;
                }
            }

            readOneTaskSendDTO.setUser_list(readOneTaskUserDTOList);
        }else if (taskType.equals("personal")){
            totalUserCount = 1;
            if(task.getTotalIsDone()){
                isDoneCount = 1;
            }

            User addUser = task.getUserTaskList().get(0).getUser();
            ReadOneTaskUserDTO readOneTaskUserDTO = new ReadOneTaskUserDTO();
            readOneTaskUserDTO.setUser_id(addUser.getId());
            readOneTaskUserDTO.setNickname(addUser.getNickname());
            readOneTaskUserDTO.setIs_done(task.getTotalIsDone());


        }else if (taskType.equals("anyone")){
            for (UserTask userTask : task.getUserTaskList()){
                //add readOneUserDTO to List.
                ReadOneTaskUserDTO readOneTaskUserDTO = getReadOneTaskUserDTO(userTask);

                //add to total user count
                totalUserCount++;

                readOneTaskUserDTOList.add(readOneTaskUserDTO);
            }
        }else{
            //error
            throw new RuntimeException("illegal task type");
        }

        readOneTaskSendDTO.setTotal_user_count(totalUserCount);
        readOneTaskSendDTO.setIs_done_count(isDoneCount);


        return readOneTaskSendDTO;
    }


    //user 의 모든 미완료 Task 조회
    //TODO: 내가 다 해도 group원이 일을 끝내지 않았다면 표시합니까?
    @Override
    public List<ReadTaskListByUserSendDTO> getTaskListForUser(User loginUser) {
        List<Task> taskList = taskRepository.findTasksByUserIdFetchUserTask(loginUser.getId());
        List<ReadTaskListByUserSendDTO> readTaskListByUserSendDTOList = new ArrayList<>();

        for(Task task : taskList){
            if(task.getTotalIsDone()){
                continue;
            }
            if(task.getUserTaskList().get(0).getIsDone()){
                continue;
            }
            ReadTaskListByUserSendDTO readTaskListByUserSendDTO = new ReadTaskListByUserSendDTO();
            readTaskListByUserSendDTO.setTask_name(task.getTaskName());
            readTaskListByUserSendDTO.setTask_id(task.getId());
            readTaskListByUserSendDTO.setTask_type(task.getTaskType());
            readTaskListByUserSendDTO.setTag_id(task.getTag().getId());
            readTaskListByUserSendDTO.setStart_time(task.getStartTime());
            readTaskListByUserSendDTO.setEnd_time(task.getEndTime());
            readTaskListByUserSendDTO.setIs_done_total(false);
            readTaskListByUserSendDTO.setIs_done_personal(false);
        }

        return readTaskListByUserSendDTOList;
    }

    @Override
    public List<TaskListReadByGroupSendDTO> readTaskListByGroup(TaskListReadByGroupReceiveDTO taskListReadByGroupReceiveDTO) {

    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private static ReadOneTaskUserDTO getReadOneTaskUserDTO(UserTask userTask) {
        User addUser = userTask.getUser();
        ReadOneTaskUserDTO readOneTaskUserDTO = new ReadOneTaskUserDTO();
        readOneTaskUserDTO.setUser_id(addUser.getId());
        readOneTaskUserDTO.setNickname(addUser.getNickname());
        readOneTaskUserDTO.setIs_done(userTask.getIsDone());
        //TODO: add profile image id field to user domain entity
        readOneTaskUserDTO.setProfile_image_id(null);
        return readOneTaskUserDTO;
    }

}
