package com.elend.p2p.workflow;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class TestTaskService {
    @Autowired
    private TaskService taskService;
    @Test
    public void test(){
        String taskId="58107";
        Task task=taskService.createTaskQuery().taskId(taskId).taskCandidateUser("zenglican").singleResult();
        System.out.println("taskId:"+task.getId());
    }
}
