package com.blate.server.mapper;

import com.blate.server.pojo.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
public interface DepartmentMapper extends BaseMapper<Department> {

    /**
     * 获取所有部门
     * @return
     */
    List<Department> getAllInFo(Integer parentId);

    /**
     * 添加部门
     * @param department
     * @return
     */
    void addDept(Department department);

    /**
     * 删除部门
     * @param department
     * @return
     */
    void deleteDept(Department department);

}
