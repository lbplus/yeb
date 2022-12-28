package com.blate.server.controller;

import com.blate.server.pojo.Admin;
import com.blate.server.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @program: yeb
 * @description: 和谁聊天
 */
@RestController
@RequestMapping("/chat")
@Api(value = "在线聊天",tags = "在线聊天")
public class ChatController {

    @Autowired
    private IAdminService iAdminService;

    @ApiOperation(value = "获取所有操作员")
    @GetMapping("/admin")
    public List<Admin> getAllAdmins(String keywords){
        //可以搜索和谁聊天
        return iAdminService.getAllAdmins(keywords);
    }

}