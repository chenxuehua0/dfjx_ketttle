package org.seaboxdata.platform.controller.system;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.seaboxdata.ext.utils.RepositoryUtils;
import org.seaboxdata.systemmng.entity.DatabaseConnEntity;
import org.seaboxdata.systemmng.entity.SlaveEntity;
import org.seaboxdata.systemmng.entity.UserEntity;
import org.seaboxdata.systemmng.entity.UserGroupAttributeEntity;
import org.seaboxdata.systemmng.service.system.CommonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by cRAZY on 2017/6/1.
 */
@Controller
@RequestMapping(value="/common")
public class CommonController {
    @Autowired
    protected CommonService cService;

    //获取所有的数据库连接
    @RequestMapping(value="/getDatabases")
    @ResponseBody
    protected void getDatabases(HttpServletResponse response,HttpServletRequest request) throws Exception{
        UserGroupAttributeEntity attr = (UserGroupAttributeEntity) request.getSession().getAttribute("userInfo");
        String userGroupName = "";
        if (null != attr) {
            userGroupName = attr.getUserGroupName();
        }
        try{
            List<DatabaseConnEntity> items=cService.getDatabases(userGroupName);
            String result= JSONArray.fromObject(items).toString();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    //
    @RequestMapping(value="/getDatabaseMeta")
    @ResponseBody
    protected void getDatabaseMeta(HttpServletResponse response,@RequestParam String databaseConn) throws Exception{
        try{

            TransMeta tra= RepositoryUtils.loadTransByPath("/test");
            DatabaseMeta inf=tra.findDatabase(databaseConn);
            String result= JSONObject.fromObject(inf).toString();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out=response.getWriter();
            out.write(result);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @RequestMapping(value="/deleteDatabaseConn")
    @ResponseBody
    protected void deleteDatabaseConn(@RequestParam String id) throws Exception{
        cService.deleteDatabaseConn(Integer.valueOf(id));
    }
}
