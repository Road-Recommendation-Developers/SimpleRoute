package process;

import dataBean.Route;
import dataBean.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import process.SimpleRoute;
import java.util.*;

public class InputData {
    public InputData() {
    }
    public List<Route> ReadRouteFile(String routefilepath) {
        String routedata = new String();
        List<Route> routeDatas = new ArrayList<Route>();
        try {
            File infile = new File(routefilepath);
            if (infile.isFile() && infile.exists()) {      // judge the file exist or not
                InputStreamReader read = new InputStreamReader(new FileInputStream(infile), "GBK");
                //InputStreamReader可以将一个字节输入流包包装成字符输入流
                BufferedReader bufferedReader = new BufferedReader(read);
                //(read);//BufferedReader在读取文本文件时，会先尽量从文件中读入字符数据并置入缓冲区，而之后若使用read()方法，会先从缓冲区中进行读取。
                String lineStr = null;
                while ((lineStr = bufferedReader.readLine()) != null) {
                    Route route = new Route(lineStr);
                    Calendar time=route.ChangeStringToCalendar(route.startLine);
                    time.add(Calendar.MINUTE,100);
                    route.setDeadLine(time.getTime().toString());
                    routeDatas.add(route);
                    //System.out.println(lineStr);
                }
                read.close();
            } else {
                System.out.println("Not Find file " + routefilepath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routeDatas;
    }
    public List<Task> ReadTaskFile(String taskfilepath) {
        String taskdata=new String();
        List<Task> taskDatas = new ArrayList<Task>();
        try {
            File infile = new File(taskfilepath);
            if (infile.isFile() && infile.exists()) {      // judge the file exist or not
                InputStreamReader read = new InputStreamReader(new FileInputStream(infile), "GBK");
                //InputStreamReader可以将一个字节输入流包包装成字符输入流
                BufferedReader bufferedReader = new BufferedReader(read);
                //(read);//BufferedReader在读取文本文件时，会先尽量从文件中读入字符数据并置入缓冲区，而之后若使用read()方法，会先从缓冲区中进行读取。
                String lineStr = null;
                while ((lineStr = bufferedReader.readLine()) != null) {
                    Task task=new Task(lineStr);
                    taskDatas.add(task);
                    //System.out.println(lineStr);

                }
                read.close();
            } else {
                System.out.println("Not Find file " + taskfilepath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskDatas;
    }

}
