package com.elend.p2p.workflow.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作类型
 * 
 * @author liyongquan
 */
public class ActionType {

    /******************************************** 流程基本操作类型 *************************************/
    /** 签收任务 */
    public final static int CLAIM_TASK = 101;

    /** 流程提单 **/
    public final static int CREATE_INSTANCE = 102;

    /** 任务处理 */
    public final static int COMPLETE_TASK = 103;

    /********************************************* 其它业务日志根据自己需要添加 *************************************/

    private static final Map<Integer, String> map = new HashMap<Integer, String>();

    static {
        map.put(CLAIM_TASK, "签收任务");
        map.put(CREATE_INSTANCE, "流程提单");
        map.put(COMPLETE_TASK, "任务处理");
    }

    public static String get(int type) {
        return map.get(type);
    }

    public static Map<Integer, String> getMap() {
        return map;
    }
}
