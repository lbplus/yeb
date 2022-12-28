package com.blate.server.service;

import com.blate.server.pojo.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blate.server.pojo.RespBean;
import com.blate.server.pojo.RespPageBean;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
public interface IEmployeeService extends IService<Employee> {

    /**
     * 分页获取员工列表
     * @param currentPage
     * @param size
     * @param employee
     * @return
     */
    RespPageBean getEmployeeList(Integer currentPage, Integer size, Employee employee, String startDate, String endDate);

    /**
     * 获取工号
     * @return
     */
    RespBean getMaxWorkID();

    /**
     * 获取工号 方法2
     * @return
     */
    RespBean getMaxWorkID2();

    /**
     * 添加员工
     * @param employee
     */
    RespBean addEmp(Employee employee);

    /**
     * 查询员工
     * @param id
     * @return
     */
    List<Employee> getEmployee(Integer id);

    /**
     * 获取所有员工工资账套
     * @param currentPage
     * @param pageSize
     * @return
     */
    RespPageBean getEmployeeWithSalary(Integer currentPage, Integer pageSize);

}
