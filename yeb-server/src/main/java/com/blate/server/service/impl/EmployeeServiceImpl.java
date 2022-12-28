package com.blate.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blate.server.mapper.EmployeeMapper;
import com.blate.server.mapper.MailLogMapper;
import com.blate.server.pojo.*;
import com.blate.server.service.IEmployeeService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author blate
 * @since 2022-07-11
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Resource
    private MailLogMapper mailLogMapper;

    /**
     * 分页获取员工列表
     *
     * @param currentPage 当前页
     * @param size        页面条数
     * @param employee    操作对象
     * @return
     */
    @Override
    public RespPageBean getEmployeeList(Integer currentPage, Integer size, Employee employee, String startDate, String endDate) {
        //开启分页
        Page<Employee> page = new Page<>(currentPage, size);
        IPage<Employee> employeePage = employeeMapper.getEmployeePage(page, employee, startDate, endDate);
        // employeePage.getTotal() 总记录数， employeePage.getRecords() 查询出来的集合(记录)
        RespPageBean respPageBean = new RespPageBean(employeePage.getTotal(), employeePage.getRecords());
        return respPageBean;
    }

    /**
     * 获取工号
     *
     * @return
     */
    @Override
    public RespBean getMaxWorkID() {
        // 获取员工编号最大值
        String maxWorkID = employeeMapper.getMaxWorkID();
        if (maxWorkID == null || maxWorkID.equals("")) {
            maxWorkID = "0001";
        }
        //转换成 integer 将最大的 员工id 加1
        String max = "1" + maxWorkID;
        Integer i1 = Integer.parseInt(max) + 1;

        //再次转换为 String 类型, 返回前端，调下一个接口将 String 类型的编号存入数据库
        String s2 = i1.toString();
        //截取 1 之后的字符串 员工编号还是8位 以 0000001 格式
        String newID = s2.substring(1);
        return RespBean.success(null, newID);
    }

    @Override
    public RespBean getMaxWorkID2() {
        // 获取员工编号最大值
        String maxWorkID = employeeMapper.getMaxWorkID();
        if (maxWorkID == null || maxWorkID.equals("")) {
            maxWorkID = "0001";
        }
        // 转换成 integer 将最大的 员工id 加 1 ， 可以保证编号唯一且自增
        Integer i = Integer.parseInt(maxWorkID) + 1;
        // 格式化用0补齐前面的位数 编号8位数 ， 该方法只能传入数字，自动格式化后转换为 String 类型
        String format = String.format("%08d", i);
        return RespBean.success(null, format);
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @Override
    public RespBean addEmp(Employee employee) {
        //计算 合同 开始日期 到 结束日期 一共多少天 beginDate：开始日期 endContract：结束日期
        LocalDate beginContract = employee.getBeginContract();
        LocalDate endContract = employee.getEndContract();
        long totalDays = beginContract.until(endContract, ChronoUnit.DAYS);// 总天数
        DecimalFormat decimalFormat = new DecimalFormat("##.00");//下面天数可能除不尽，所以保留两位小数
        String format = decimalFormat.format(totalDays / 365.00);// 合同年限
        Double totalYears = Double.parseDouble(format);// 总年数
        employee.setContractTerm(totalYears);// 合同期限
        if (employeeMapper.insert(employee) == 1) {// insert 方法返回受影响的行数，受影响的行数如果为1，则说明成功
            Employee emp = employeeMapper.getEmployee(employee.getId()).get(0);
            String msgId = UUID.randomUUID().toString();
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(msgId)
                    .setEid(emp.getId())
                    .setStatus(0)
                    .setExchange(MailConstants.MAIL_EXCHANGE_NAME)
                    .setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME)
                    .setCount(0)
                    .setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT))//1分钟后开始重试
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            mailLogMapper.insert(mailLog);//第一次发送消息落库标记为发送中
            //发送消息
//            rabbitTemplate.convertAndSend(MailConstants.MAIL_QUEUE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, emp, new CorrelationData(msgId));
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, emp, new CorrelationData(msgId));
            return RespBean.success("添加成功!");

        }
        return RespBean.error("添加失败！");
    }

    /**
     * 查询员工
     *
     * @param id
     * @return
     */
    @Override
    public List<Employee> getEmployee(Integer id) {
        return employeeMapper.getEmployee(id);
    }

    /**
     * 获取所有员工工资账套
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public RespPageBean getEmployeeWithSalary(Integer currentPage, Integer pageSize) {
        //开启分页
        //传入当前页，每页大小
        Page<Employee> employeePage = new Page<>(currentPage, pageSize);
        IPage<Employee> iPage = employeeMapper.getEmployeeWithSalary(employeePage);
        //iPage.getTotal() 总条数 ， 具体的数据 iPage.getRecords()
        RespPageBean respPageBean = new RespPageBean(iPage.getTotal(), iPage.getRecords());
        return respPageBean;
    }

}
