package com.elend.p2p.workflow.sqlbuilder;

import org.apache.commons.lang3.StringUtils;

import com.elend.p2p.workflow.vo.CommentSearchVO;

/**
 * 批注sql生成
 * @author mgt
 *
 */
public class CommentSqlBuilder {
    public String list(CommentSearchVO svo) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append("c.ID_ as commentId,");
        sb.append("c.TYPE_ as type,");
        sb.append("c.TIME_ as time,");
        sb.append("c.USER_ID_ as userId,");
        sb.append("c.TASK_ID_ as taskId,");
        sb.append("c.PROC_INST_ID_ as processInstanceId,");
        sb.append("c.ACTION_ as action,");
        sb.append("c.MESSAGE_ as message, ");
        sb.append("c.FULL_MSG_ as fullMessage ");
        sb.append("from ACT_HI_COMMENT c ");
        sb.append("where TYPE_ = 'comment' ");
        
        if(StringUtils.isNotBlank(svo.getUserId())) {
            sb.append("and USER_ID_ = #{userId} ");
        }
        
        if(StringUtils.isNotBlank(svo.getProcessInstanceId())) {
            sb.append("and PROC_INST_ID_ = #{processInstanceId} ");
        }
        
        if(StringUtils.isNotBlank(svo.getStartTime())) {
            sb.append("and TIME_ >= #{startTime} ");
        }
        
        if(StringUtils.isNotBlank(svo.getEndTime())) {
            sb.append("and TIME_ <= #{endTime} ");
        }
        
        sb.append("ORDER BY TIME_ DESC ");
        sb.append("LIMIT #{start} , #{size} ");
        return sb.toString();
    }
    
    public String count(CommentSearchVO svo) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append("count(*) ");
        sb.append("from ACT_HI_COMMENT c ");
        sb.append("where TYPE_ = 'comment' ");
        
        if(StringUtils.isNotBlank(svo.getUserId())) {
            sb.append("and USER_ID_ = #{userId} ");
        }
        
        if(StringUtils.isNotBlank(svo.getProcessInstanceId())) {
            sb.append("and PROC_INST_ID_ = #{processInstanceId} ");
        }
        
        if(StringUtils.isNotBlank(svo.getStartTime())) {
            sb.append("and TIME_ >= #{startTime} ");
        }
        
        if(StringUtils.isNotBlank(svo.getEndTime())) {
            sb.append("and TIME_ <= #{endTime} ");
        }
        
        return sb.toString();
    }
}
