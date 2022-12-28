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
package com.blate.server.exception;

import com.blate.server.pojo.RespBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */

@RestControllerAdvice//表示控制器的主增强类
public class GlobalException {

    @ExceptionHandler(value = SQLException.class)
    public RespBean mySqlException(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {//外键约束
            return RespBean.error("该数据有关联数据,操作失败！");
        }
        return RespBean.error("数据库异常，操作失败！");
    }

}
