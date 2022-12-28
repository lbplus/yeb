package com.blate.server.utils;

import com.blate.server.pojo.Admin;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @program: yeb
 * @description: 操作员工具类
 */
public class AdminUtils {

    /**
     * 获取当前登录操作员
     * @return
     */
    public static Admin getCurrentAdmin(){
        return (Admin)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}