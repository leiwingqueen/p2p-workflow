package com.elend.p2p.workflow;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.workflow.vo.UserInfo;

/**
 * 用户数据（加载到内存）
 * @author liyongquan
 *
 */
@Component
public class WorkflowUserInfoData extends TimerTask{
    protected static Log logger = LogFactory.getLog(WorkflowUserInfoData.class);
    @Autowired
    private IdentityService identityService;
    
    private Map<String,UserInfo> userMap;
    
    public WorkflowUserInfoData(){
        userMap=new HashMap<String, UserInfo>();
    }
    
    /**
     * 数据从activiti加载到内存
     */
    public void load(){
        logger.info("start load user data.");
        long startTime=new Date().getTime();
        List<User> users=identityService.createUserQuery().list();
        for(User user:users){
            UserInfo userInfo=new UserInfo();
            userInfo.setUsername((user.getLastName()==null?"":user.getLastName())+user.getFirstName());
            userInfo.setId(user.getId());
            userMap.put(user.getId(), userInfo);
        }
        long endTime=new Date().getTime();
        logger.info("load user data end .use time is:"+(endTime-startTime)+"ms");
    }
    
    /**
     * 根据ID获取用户名
     * @param id
     * @return
     */
    public String getUsername(String id){
        UserInfo user=userMap.get(id);
        if(user==null)return "";
        return user.getUsername();
    }

    @Override
    public void run() {
        load();
    }
}
