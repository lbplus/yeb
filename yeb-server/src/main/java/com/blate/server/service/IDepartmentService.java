package com.blate.server.service;

import com.blate.server.pojo.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blate.server.pojo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
public interface IDepartmentService extends IService<Department> {

    /**
     * 获取所有部门
     * @return
     */
    List<Department> getAllInFo();

    /**
     * 添加部门
     * @param department
     * @return
     */
    RespBean addDept(Department department);

    /**
     * 删除部门
     * @param id
     * @return
     */
    RespBean deleteDept(Integer id);

}
