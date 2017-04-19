package com.elend.p2p.workflow.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.cmd.CustomSqlExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.elend.p2p.util.OrderIdHelper;
import com.elend.p2p.workflow.mapper.CommentMapper;
import com.elend.p2p.workflow.vo.CommentSearchVO;
import com.elend.p2p.workflow.vo.CommentVO;

/**
 * JAVA service测试
 * @author liyongquan 2015年12月22日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class TestJavaServiceTest {
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private ManagementService managementService;
    
    @Test
    public void testExecute() {
        //启动流程
        String processDefinitionKey="process";
        String businessKey=OrderIdHelper.newOrderId();
        Map<String, Object> variables=new HashMap<String, Object>(8);
        variables.put("input", "hello world");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey,
                                                                                   businessKey,
                                                                                   variables);
        if(processInstance!=null){
            System.out.println("启动流程成功...");
            return;
        }
        System.out.println("启动流程失败..");
    }
    
    @Test
    public void testComments() {
        Task singleResult = taskService.createTaskQuery().taskId("235606").singleResult();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc().list();
        //List<Comment> processInstanceComments = taskService.getProcessInstanceComments(singleResult.getProcessInstanceId());
        List<Comment> processInstanceComments = taskService.getProcessInstanceComments("235601");
        for (Comment c : processInstanceComments) {
            System.out.println("=========================================>" + c.getId() + ":" + c.getFullMessage());
        }
        
        HistoricTaskInstance singleResult2 = historyService.createHistoricTaskInstanceQuery().taskId("235606").singleResult();
        System.out.println("==================" + singleResult2.getName());
        
        List<HistoricVariableInstance> list2 = historyService.createHistoricVariableInstanceQuery().processInstanceId("235601").list();
        for(HistoricVariableInstance i : list2) {
            System.out.println("==========================" + i.getId() + ":" + i.getVariableName() + ":" + i.getValue());
        }
        
        
        List<Comment> commentsByType = taskService.getCommentsByType("comment");
        for (Comment c : commentsByType) {
            System.out.println("=========================================>" + c.getId() + ":" + c.getFullMessage());
        }
        
        
        
    }
    
    @Test
    public void testSql() {
        CustomSqlExecution<CommentMapper, List<CommentVO>> customSqlExecution = new AbstractCustomSqlExecution<CommentMapper, List<CommentVO>>(CommentMapper.class) {
            public List<CommentVO> execute(
                    CommentMapper customMapper) {
                CommentSearchVO svo = new CommentSearchVO();
                return customMapper.list(svo);
            }
        };

        List<CommentVO> list = managementService.executeCustomSql(customSqlExecution);
        System.out.println(list);
        
    }

}
