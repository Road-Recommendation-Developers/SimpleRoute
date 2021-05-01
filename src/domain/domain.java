package domain;

import dataBean.Route;
import dataBean.Task;
import process.InputData;
import process.SimpleRoute;

import java.text.ParseException;
import java.util.List;

public class domain {

        public static void main(String[] args) throws ParseException {
            long startTime = System.currentTimeMillis();        //获取开始时间
            String routepath = "src\\Data\\course.txt";         //路线文件路径
            String taskpath = "src\\Data\\task.txt";            //事务文件路径
            InputData ipd=new InputData();
            List<Route> routeDatas=ipd.ReadRouteFile(routepath); //完成文件读取
            List<Task> taskDatas=ipd.ReadTaskFile(taskpath);    //完成文件读取
            SimpleRoute sim=new SimpleRoute();
            //sim.DrawTheRoute(routeDatas);
            List<Route> todoList=sim.GetTheTodoListWithInTimeWindow(routeDatas,taskDatas); //建立窗口
            List<Route> orderedList=sim.Conclude(routeDatas);

            long endTime = System.currentTimeMillis();          //获取结束时间
            System.out.println("程序运行时间：" + (endTime - startTime) + "ms");    //输出程序运行时间
        }

}
