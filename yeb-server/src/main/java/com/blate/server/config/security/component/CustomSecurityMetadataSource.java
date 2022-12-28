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
package com.blate.server.config.security.component;

import com.blate.server.pojo.Menu;
import com.blate.server.pojo.Role;
import com.blate.server.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

/**
 * 权限控制
 * 根据请求url分析请求所需的角色
 * 假如请求访问admin/login，就是拿整个系统的url去匹配，如果匹配上了一个就加上对应的角色，遍历完系统所有url就知道这个url需要哪些角色才能访问（类似白名单）
 * 如果遍历一圈都没有这个admin/login，就随便创建一个角色权限，这里就是一个登录就可以访问的角色不需要授权
 */
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {//为当前的访问规则进行决策，是否给予访问的权限

    @Autowired
    private IMenuService menuService;

    AntPathMatcher antPathMatcher=new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {//传参数就是受保护对象
        //获取的请求url
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        for (Menu menu : menuService.getMenuWithRole()) {
            //判断请求url是否和菜单（也就是url）匹配，如果匹配就讲这个菜单所对应的角色添加到集合中
            if(antPathMatcher.match(menu.getUrl(),requestUrl)){
                String[] str = menu.getRoles().stream().map(Role::getName).toArray(String[]::new);
                return SecurityConfig.createList(str);
            }
        }
        //没匹配的url默认登录即可访问（就是不用角色都行）
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {//该方法可以用来返回所有的权限属性，以便在项目启动阶段做校验，如果不需要校验，则直接返回 null 即可
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {//该方法表示当前对象支持处理的受保护对象是 FilterInvocation
        return false;
    }

}
