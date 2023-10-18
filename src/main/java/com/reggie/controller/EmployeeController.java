package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.management.LockInfo;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        /**
         * 1,将页面提交的密码password进行md5加密
         * 2,根据页面提交的用户名username查询数据库
         * 3,如果没有查询到则返回登录失败结果
         * 4,密码比对，如果不一致则进行返回登录失败结果
         * 5,查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6,登录成功，将员工id存入Session并返回登陆成功结果
         */


        //1,将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2,根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);


        //3,如果没有查询到则返回登录失败结果
        if (emp==null){
            return R.error("登录失败");
        }

        //4,密码比对，如果不一致则进行返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5,查看员工状态，如果为已禁用状态，则返回员工已禁用结果   0表示禁用，1表示未禁用
        if (emp.getStatus()==0){
            return R.error("账号已禁用");
        }

        //6,登录成功，将员工id存入Session并返回登陆成功结果
       request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //移除session对象
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 添加用户
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){

        //使用MD5加密  设置初始化密码
        employee.setPassword(DigestUtils.md5DigestAsHex("1234560".getBytes()));

        //使用自动填充
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        //Long empId=(Long)request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页查询
        Page pageInfo=new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<>();

        //添加条件
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
       /* default <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
            return this.getBaseMapper().selectPage(page, queryWrapper);
        }*/
        employeeService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据id修改员工信心
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){


        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        //修改时自动填充
        //Long empId = (Long) request.getSession().getAttribute("employee");
       // employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("修改员工成功");
    }


    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工");
    }
}
