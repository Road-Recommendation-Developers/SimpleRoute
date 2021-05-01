package domain;

import dataBean.Route;
import dataBean.Task;
import process.InputData;
import process.SimpleRoute;

import java.text.ParseException;
import java.util.List;

public class domain {

        public static void main(String[] args) throws ParseException {
            long startTime = System.currentTimeMillis();        //��ȡ��ʼʱ��
            String routepath = "src\\Data\\course.txt";         //·���ļ�·��
            String taskpath = "src\\Data\\task.txt";            //�����ļ�·��
            InputData ipd=new InputData();
            List<Route> routeDatas=ipd.ReadRouteFile(routepath); //����ļ���ȡ
            List<Task> taskDatas=ipd.ReadTaskFile(taskpath);    //����ļ���ȡ
            SimpleRoute sim=new SimpleRoute();
            //sim.DrawTheRoute(routeDatas);
            List<Route> todoList=sim.GetTheTodoListWithInTimeWindow(routeDatas,taskDatas); //��������
            List<Route> orderedList=sim.Conclude(routeDatas);

            long endTime = System.currentTimeMillis();          //��ȡ����ʱ��
            System.out.println("��������ʱ�䣺" + (endTime - startTime) + "ms");    //�����������ʱ��
        }

}
