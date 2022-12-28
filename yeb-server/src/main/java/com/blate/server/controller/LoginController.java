/**
 * @author Blate
 * @BiuBiuBiu ****************常用快捷键如下****************
 * ctrl+J：显示所有快捷键
 * itit: 快速出while循环
 * ctrl+alt+L: 格式化代码
 * ctrl+alt+T:捕获异常
 * ctrl+B:追当前光标所指方法
 * ctrl+shift+T:创建测试方法
 * ctrl+h/r:批量查找替换
 * ctrl+shift+h/r:大批量查找替换
 * /**+回车:快速生成方法注释
 * ctrl+F12:查看类结构
 * ctrl+alt+V:自动出返回值
 * Alt+enter:自动出方法或者实现接口
 * ctrl+shift+Y:翻译
 * ctrl+p:查看填入参数提示
 * ctrl+d:快速复制手动替换
 * control+option+0:清理无用的包
 * control+h:查看类继承图
 * command+n:生成构造函数
 * control+0:快速选择要覆盖的方法
 */
package com.blate.server.controller;

import com.blate.server.pojo.Admin;
import com.blate.server.pojo.AdminLoginParam;
import com.blate.server.pojo.RespBean;
import com.blate.server.service.IAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


@Api(tags = "用户登录")
@RestController
public class LoginController {

    @Autowired
    private IAdminService adminService;

    @ApiOperation("登录之后返回Token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginParam adminLoginParam, HttpServletRequest request){
        return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword(),adminLoginParam.getCaptcha(),request);
    }

    @ApiOperation(value = "获取当前登陆用户信息")
    @GetMapping("/admin/info")
    public Admin getAdminInfo(Principal principal){
        if(null==principal){
            return null;
        }
        String username =principal.getName();
        Admin admin=adminService.getAdminByUserName(username);
        admin.setPassword(null);
        admin.setRoles(adminService.getRoles(admin.getId()));
        return admin;
    }

    @ApiOperation(value = "退出登陆")
    @PostMapping("/logout")
    public RespBean logout() {
        return RespBean.success("注销成功");
    }


}
