package org.seaboxdata.systemmng.utils.quartz;

import org.pentaho.di.core.Const;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.seaboxdata.ext.JobExecutor;
import org.seaboxdata.ext.job.JobExecutionConfigurationCodec;
import org.seaboxdata.ext.utils.JSONObject;
import org.seaboxdata.ext.utils.RepositoryUtils;
import org.seaboxdata.systemmng.entity.SlaveEntity;
import org.seaboxdata.systemmng.entity.UserEntity;
import org.seaboxdata.systemmng.utils.task.CarteClient;
import org.seaboxdata.systemmng.utils.task.CarteTaskManager;

/**
 * Created by cRAZY on 2017/3/6.
 * 定时作业具体方法 by quartz
 */
public class JobTimerTask implements Job{
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobExecutor jobExecutor = (JobExecutor)context.getJobDetail().getJobDataMap().get("jobExecutor");
        UserEntity user=(UserEntity)context.getJobDetail().getJobDataMap().get("loginUser");
        try {
           /*   使用与carte服务器交互的方式执行定时作业
           CarteClient cc=new CarteClient(slave);
            //拼接资源库名
            String repoId=CarteClient.hostName+"_"+CarteClient.databaseName;
            //节点执行作业的请求
            String urlString="/?rep="+repoId+"&user="+user.getLogin()+"&pass="+user.getPassword()+"&job="+path+"&level=Basic";
            urlString = Const.replace(urlString, "/", "%2F");
            urlString = cc.getHttpUrl() + CarteClient.EXECREMOTE_JOB +urlString;
            System.out.println("定时作业请求的url字符串为" + urlString);
            CarteTaskManager.addTask(cc, "job_exec", urlString);*/
            //调用kettle api方式执行定时作业
            Thread tr = new Thread(jobExecutor, "JobExecutor_" + jobExecutor.getExecutionId());
            tr.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
