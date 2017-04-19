package com.elend.p2p.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

import com.elend.p2p.workflow.sqlbuilder.CommentSqlBuilder;
import com.elend.p2p.workflow.vo.CommentSearchVO;
import com.elend.p2p.workflow.vo.CommentVO;

public interface CommentMapper {
    @SelectProvider(type = CommentSqlBuilder.class, method="list")
    List<CommentVO> list(CommentSearchVO svo);
    
    @SelectProvider(type = CommentSqlBuilder.class, method="count")
    int count(CommentSearchVO svo);
}
