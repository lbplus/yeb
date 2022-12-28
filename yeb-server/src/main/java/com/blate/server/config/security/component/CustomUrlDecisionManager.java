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

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 权限控制
 * 判断这个请求的url要不要权限，如果不要即默认登录就可以访问，放过去算了
 * 如果需要角色就get前面设置的角色，然后获取当前这个用户是个啥角色，这里在Admin就重写了getAuthorities方法，目的就是获取当前用户拥有哪些角色
 *
 * 总的思路就是，用户请求一个资源一般就是url，这里属于权限层面
 * 然后判断用户有没有这个权限
 * 这是两个层面，于是他们通过转换全部到角色层面比较
 * 也就是 url转换成需要哪些角色才能访问
 * 然后  用户就转换成用户具有哪些角色
 * 他们再比较角色，如果匹配则有权限，匹配不上就没有权限也就是著名的RBAC理论，基于角色的访问权限控制
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {//基于AccessDecisionVoter实现权限认证的官方UnanimousBased


    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        for (ConfigAttribute configAttribute:configAttributes){
            //当前url所需要的角色
            String needRole = configAttribute.getAttribute();//metaSource已经给他匹配了对应的角色，这里获取即可
            if("ROLE_LOGIN".equals(needRole)){//判断角色是否随便给的登录就能访问的角色，此角色在CustomFilter中设置
                //判断是否登录
                if(authentication instanceof AnonymousAuthenticationToken){
                    throw new AccessDeniedException("尚未登录，请登录");
                }else{
                    return;
                }
            }
            //到这里就说明不是随便登录就可以访问。我们需要判断用户角色是否为url所需角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();//这里在Admin里重写方法了
            for (GrantedAuthority authority : authorities) {//遍历用户的角色集合中是否有一个是这个url需要的，有就放行，否则抛出异常
                if(authority.getAuthority().equals(needRole)){
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足，请联系管理员");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
