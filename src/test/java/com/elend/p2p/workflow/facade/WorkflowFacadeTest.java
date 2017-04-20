package com.elend.p2p.workflow.facade;

import static org.junit.Assert.fail;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.elend.p2p.PageInfo;
import com.elend.p2p.workflow.vo.TaskDetailVO;
import com.elend.p2p.workflow.vo.TaskSearchVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class WorkflowFacadeTest {
    @Autowired
    private WorkflowFacade workflowFacade;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Test
    public void testClaim() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreate() {
        fail("Not yet implemented");
    }

    @Test
    public void testComplete() {
        fail("Not yet implemented");
    }

    @Test
    public void testEndProcessByProcessInstanceId() {
        fail("Not yet implemented");
    }

    @Test
    public void testCreatePage() {
        fail("Not yet implemented");
    }

    @Test
    public void testCompletePage() {
        fail("Not yet implemented");
    }

    @Test
    public void testMCompletePage() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetDetailPage() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetInstanceDetail() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetTaskDetail() {
        fail("Not yet implemented");
    }

    @Test
    public void testTodoTaskList() {
        fail("Not yet implemented");
    }

    @Test
    public void testClaimTaskList() {
        PageInfo<TaskDetailVO> pageInfo=workflowFacade.claimTaskList(new TaskSearchVO(), "liyongquan");
        System.out.println("list:"+pageInfo.getList());
    }

    @Test
    public void testGetAllMyApply() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRunningApply() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetFinishApply() {
        fail("Not yet implemented");
    }

    @Test
    public void testFindBackAvtivity() {
        fail("Not yet implemented");
    }

    @Test
    public void testBackProcess() {
        fail("Not yet implemented");
    }

    @Test
    public void testQueryHistoricProcessInstance() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetHistoryAudit() {
        fail("Not yet implemented");
    }

    @Test
    public void testCommentList() {
        fail("Not yet implemented");
    }
    
    @Test
    public void testGetCategory1(){
        ProcessDefinition queryResult=repositoryService.createProcessDefinitionQuery().orderByDeploymentId().desc().list().get(0);
        System.out.println("name:"+queryResult.getName());
        System.out.println("getDeploymentId:"+queryResult.getDeploymentId());
        System.out.println("category:"+queryResult.getCategory());
        System.out.println("getTenantId:"+queryResult.getTenantId());
        
    }
    
    @Test
    public void testGetCategory2(){
        String newTenantId="testAppId";
        Task task = taskService.createTaskQuery().includeProcessVariables().taskTenantId(newTenantId).orderByTaskId().desc().list().get(0);
        System.out.println("taskName:"+task.getName());
        System.out.println("category:"+task.getCategory());
        System.out.println("getTenantId:"+task.getTenantId());
    }
    
    @Test
    public void changeTenantId(){
        String deploymentId="94976";
        String newTenantId="testAppId";
        repositoryService.changeDeploymentTenantId(deploymentId, newTenantId);
    }
    
    @Test
    public void testDeployment(){
        String path="";
        String newTenantId="testAppId";
        Deployment deploy=repositoryService.createDeployment().addClasspathResource(path).tenantId(newTenantId).deploy();
        System.out.println("deploy:"+deploy.getId());
    }
}
