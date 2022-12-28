package com.blate.server.controller;

import com.blate.server.pojo.Admin;
import com.blate.server.pojo.RespBean;
import com.blate.server.service.IAdminService;
//import com.blate.server.utils.FastDFSUtils;
import com.blate.server.utils.FastDFSUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @program: yeb
 * @description: 用户个人中心
 */
@RestController
@Api(value = "个人中心",tags = "个人中心")
public class AdminInfoController {
    @Autowired
    private IAdminService iAdminService;

    @ApiOperation(value = "更新当前用户信息")
    @PutMapping("/admin-info")
    // Authentication 这个对象是 Security 提供的可以用他直接获取到当前登录用户的信息，Admin 是前端用户传过来的用户信息
    public RespBean updateAdmin(@RequestBody Admin admin, Authentication authentication){
        if (iAdminService.updateById(admin)){//只是跟新了数据库
            /**
             * 1.用户对象
             * 2.凭证（密码）
             * 3.用户角色
             */
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(admin, authentication.getCredentials(), authentication.getAuthorities()));
            return RespBean.success("更新成功");
        }
        return RespBean.error("更新失败!");
    }

    @ApiOperation(value = "更新用户密码")
    @PutMapping("/admin-pass")
    public RespBean updateAdminPassWord(@RequestBody Map<String,Object> info){
        //旧密码
        String oldPass = (String) info.get("oldPass");
        //新密码
        String pass = (String) info.get("pass");
        //当前登录用户ID
        Integer adminId = (Integer) info.get("adminId");
        return iAdminService.updateAdminPassWord(oldPass,pass,adminId);
    }

    //这个功能需要自己找到包以后再用，详情请看pom.xml
    @ApiOperation(value = "更新用户头像")
    @PostMapping("/admin/userface")
    public RespBean updateAdminUserFace(MultipartFile file,
                                        Integer id,
                                        Authentication authentication){
        //上传之后的文件名
        String[] filePath = FastDFSUtils.upload(file);
        //获取 url ， 初始文件名，上传之后的文件名
        String url = FastDFSUtils.getTrackerUrl() + filePath[0] + "/" + filePath[1];
        return iAdminService.updateAdminUserFace(url,id,authentication);
    }

}