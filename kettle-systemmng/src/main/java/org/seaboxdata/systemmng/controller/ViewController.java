package org.seaboxdata.systemmng.controller;

import org.flhy.ext.utils.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;
import org.seaboxdata.systemmng.service.ControlService;
import org.seaboxdata.systemmng.service.SchedulerService;
import org.seaboxdata.systemmng.service.SlaveService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created by cRAZY on 2017/6/5.
 *  平台概况模块
 */
@Controller
@RequestMapping(value="/viewModule")
public class ViewController {
    @Autowired
    protected ControlService controlService;
    @Autowired
    protected SlaveService slaveService;
    @Autowired
    protected SchedulerService schedulerService;

    //获取平台模块的数据
    @RequestMapping(value="/getData")
    @ResponseBody
    protected void getDatabases(HttpServletResponse response,HttpServletRequest request) throws Exception{
        try{
            UserGroupAttributeEntity attr=(UserGroupAttributeEntity)request.getSession().getAttribute("userInfo");
            String userGroupName="";
            if(null!=attr){
                userGroupName=attr.getUserGroupName();
            }
            Integer runningJobCount=controlService.getAllRunningJob(userGroupName).size();
            Integer runningTransCount=controlService.getAllRunningTrans(userGroupName).size();
            Integer slaveCount=slaveService.getAllSlaveSize();
            Integer schedulerCount=schedulerService.getSchedulerJobByLogin(userGroupName).size();

            JSONObject result=new JSONObject();

            JSONObject runningJob=new JSONObject();
            runningJob.put("value",runningJobCount);
            runningJob.put("name","运行中作业数");
            JSONObject runningTrans=new JSONObject();
            runningTrans.put("value",runningTransCount);
            runningTrans.put("name","运行中转换数");
            JSONObject slave=new JSONObject();
            slave.put("value",slaveCount);
            slave.put("name","节点数");
            JSONObject scheduler=new JSONObject();
            scheduler.put("value",schedulerCount);
            scheduler.put("name","定时作业数");

            result.put("runningJob",runningJob);
            result.put("runningTrans",runningTrans);
            result.put("slave",slave);
            result.put("scheduler",scheduler);


            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result.toString());
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
