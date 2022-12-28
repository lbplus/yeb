package com.blate.server.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blate.server.pojo.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 分页获取员工列表
     * @param page
     * @param employee
     */
    IPage<Employee> getEmployeePage(@Param("page") Page<Employee> page, @Param("employee") Employee employee,
                                    @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取员工最大编号
     * @return
     */
    String getMaxWorkID();

    /**
     * 查询员工
     * @param
     * @return
     */
    List<Employee> getEmployee(Integer id);

    /**
     * 获取所有员工套账
     * @param page
     * @return
     */
    IPage<Employee> getEmployeeWithSalary(Page<Employee> page);

}
