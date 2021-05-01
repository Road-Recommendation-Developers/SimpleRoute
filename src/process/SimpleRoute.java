package process;

import dataBean.Route;
import dataBean.Task;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleRoute {
    public SimpleRoute() {
    }

    public List<Route> GetTheTodoListWithInTimeWindow(List<Route> routeDatas, List<Task> taskDatas) throws ParseException {
        List<Route> todoList=new ArrayList<Route>();
        //FormateTheInfos(routeDatas);
        Calendar start = Calendar.getInstance();
        start.set(Calendar.MONTH,3);
        start.set(Calendar.DATE,12);
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.MONTH,3);
        end.set(Calendar.DATE,15);
        end.set(Calendar.HOUR_OF_DAY,0);
        end.set(Calendar.MINUTE,0);
        end.set(Calendar.SECOND,0);
        //System.out.println(start.getTime().toString()+end.getTime().toString());
        //输出4.12-4.16这个时间段的课程情况与待办事务安排情况，窗口为3
        while (start.get(Calendar.DATE)<=14){
            //内存释放
            todoList.clear();
            System.out.println("The course between Apr "+start.get(Calendar.DATE)+" and Apr "+end.get(Calendar.DATE)+" is:");
            //插入课程数据
            for(Route r : routeDatas ) {
                Calendar tmp=Calendar.getInstance();
                tmp=r.ChangeStringToCalendar(r.getStartLine());
                //设置窗口大小为3
                if (tmp.after(start)&& tmp.before(end)) {
                    //在窗口内，则存储进去
                    todoList.add(r);
                    System.out.println(r);
                }
            }
            List<Route> freeTimeTable=GetTheFreeTimeTable(todoList);
            //插入待办事务
            List<Task> tmpTask = new ArrayList<>(taskDatas);
            InsertTaskIntoTodoList(freeTimeTable,tmpTask,todoList);

            start.add(Calendar.DATE,1);
            end.add(Calendar.DATE,1);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("--------------");
            //暂时发现问题，时间排序上
            //todoList.sort(Comparator.comparing(Route::getStartLine));
            todoList.sort(new Comparator<Route>() {
                @Override
                public int compare(Route x, Route y) {
                    Calendar calendar_x= null;
                    Calendar calendar_y= null;
                    try {
                        calendar_x = x.ChangeStringToCalendar(x.getStartLine());
                        calendar_y = y.ChangeStringToCalendar(y.getStartLine());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    assert calendar_x != null;
                    return calendar_x.compareTo(calendar_y);
                }
            }
            );
            //希望是日期而非时间，相应的办法？
            //绘制出相应的路线，有个直观认识
            DrawTheRoute(todoList);
            EstimateTheEffect(todoList);
            //writeFile(todoList);
            System.out.println();
        }
        return todoList;
    }

    public void FormateTheInfos(List<Route> routeDatas){
        Calendar time = Calendar.getInstance();
        int day,hour;
        time.set(Calendar.DATE,12);
        List<Route> formatList=new ArrayList<Route>();
        Map<String, Integer> cnt = new LinkedHashMap<String, Integer>() ;

        for ( Route r : routeDatas ) {
            Integer last = cnt.get(r.getStartLine().substring(1,2));
            if (last != null) {
                cnt.put(r.getStartLine().substring(1,2), last+1);
            } else {
                cnt.put(r.getStartLine().substring(1,2), 1);
            }
        }
        for ( String key : cnt.keySet() ) {
            day=ChangeKeyToDay(key);
            time.set(Calendar.DATE,day);//bug根源
            for(Route r : routeDatas ) {
                if (r.getStartLine().substring(1,2).equals(key)) {
                    hour=ChangeWordToIntTime(r.getStartLine().substring(2,6));
                    time.set(Calendar.HOUR_OF_DAY,hour);
                    time.set(Calendar.MINUTE,0);
                    time.set(Calendar.SECOND,0);
                    r.setStartLine(time.getTime().toString());
                    r.setExecuteTime(100);
                    r.setType("course");
                    formatList.add(r);
                }
            }
        }
        writeFile(formatList);

    }
    public String ChangeWordToTime(String Word){
        String time=null;
        if(Word.equals("1-2节"))
            time="8:00-9:40";
        if(Word.equals("3-4节"))
            time="10:00-11:40";
        if(Word.equals("5-6节"))
            time="14:00-15:40";
        if(Word.equals("7-8节"))
            time="16:00-17:40";
        if(Word.equals("9-10节"))
            time="19:00-20:40";
        return time;
    }

    public int ChangeWordToIntTime(String Word){
        if(Word.equals("1-2节"))
            return 8;
        else if(Word.equals("3-4节"))
            return 10;
        else if(Word.equals("5-6节"))
            return 14;
        else if(Word.equals("7-8节"))
            return 16;
        else if(Word.equals("9-10节"))
            return 19;
        else return 0;
    }
    public int ChangeKeyToDay(String key){
        if(key.equals("一"))
            return 12;
        else if(key.equals("二"))
            return 13;
        else if(key.equals("三"))
            return 14;
        else if(key.equals("四"))
            return 15;
        else if(key.equals("五"))
            return 16;
        else return 0;
        //意外情况的处理，还需加以考虑的
    }

    public boolean InTimeWindow(Calendar endTime,Calendar windowTime){
        if(windowTime.compareTo(endTime)>0)
            return true;
        else
            return false;
    }

    public void DrawTheRoute(List<Route> routeDatas){
        String startPoint,endPoint,startLine,deadLine;
        String startLat,startLog,endLat,endLog;
        String type;
        int executeTime;
        for(int i=0;i<routeDatas.size();i++){
            startPoint=routeDatas.get(i).getStartPoint();
            startLat=routeDatas.get(i).getStartLat();
            startLog=routeDatas.get(i).getStartLog();
            endPoint=routeDatas.get(i).getEndPoint();
            endLat=routeDatas.get(i).getEndLat();
            endLog=routeDatas.get(i).getEndLog();
            startLine=routeDatas.get(i).getStartLine();
            executeTime=routeDatas.get(i).getExecuteTime();
            deadLine=routeDatas.get(i).getDeadLine();
            type=routeDatas.get(i).getType();
            System.out.println("The route is from "+startPoint+" to "+endPoint+",the start point location is:"+startLat+","+startLog+",the end point location is:"+endLat+","+endLog+",the start line is "+startLine+",the dead line is "+deadLine+",the execute time is "+executeTime+",the type is "+type);

        }
    }
    public static void writeFile(List<Route> formatList) {
        try {
            File file = new File("src\\Data\\output.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            FileOutputStream fos = null;
            if(!file.exists()){
                file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(file);//首次写入获取
            }else{
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }
            OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");//指定以GBK格式写入文件
            for(Route r:formatList) {
                osw.append(String.valueOf(r)).append("\r\n"); // \r\n即为换行
                osw.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Route> Conclude(List<Route> routeDatas){
        //处理目标：对涉及的地点进行归类，输出排序后的List
        // System.out.println("---------------------");
        List<Route> orderedList=new ArrayList<Route>();
        Map<String, Integer> cnt = new TreeMap<String, Integer>();
        for ( Route r : routeDatas ) {
            Integer last = cnt.get(r.getStartPoint());
            if (last != null) {
                cnt.put(r.getStartPoint(), last+1);
            } else {
                cnt.put(r.getStartPoint(), 1);
            }
        }

        for ( String key : cnt.keySet() ) {
            for(Route r : routeDatas ) {
                if (r.getStartPoint().equals(key)) {
                    orderedList.add(r);
                }
            }
        }
        return orderedList;
    }

    public List<Route> GetTheFreeTimeTable(List<Route> todoList) throws ParseException {
        //输入：待办课程事务
        //输出：空闲时间序列
        List<Route> freeTimeTable=new ArrayList<>();
        for(int i=0;i<todoList.size()-1;i++){
            Route tmp=new Route();
            tmp.type="free";
            Calendar DeadLineday=Calendar.getInstance();
            DeadLineday=tmp.ChangeStringToCalendar(todoList.get(i).getDeadLine());
            Calendar StartLineday=Calendar.getInstance();
            StartLineday=tmp.ChangeStringToCalendar(todoList.get(i+1).getStartLine());
            if(DeadLineday.get(Calendar.DATE)<StartLineday.get(Calendar.DATE)){
                //即为不同的两天，应考虑约束条件；
                //约束条件2，23:00-7:00之间不能安排事务；
                tmp.setStartLine(DeadLineday.getTime().toString());
                DeadLineday.set(Calendar.HOUR_OF_DAY,23);
                DeadLineday.set(Calendar.MINUTE,0);
                DeadLineday.set(Calendar.SECOND,0);
                tmp.setDeadLine(DeadLineday.getTime().toString());
                freeTimeTable.add(tmp);
                //考虑第二天的待办事务的起始时间不是7：00
                if(StartLineday.get(Calendar.HOUR_OF_DAY)>=7){
                    DeadLineday.set(Calendar.DATE,StartLineday.get(Calendar.DATE));
                    DeadLineday.set(Calendar.HOUR_OF_DAY,7);
                    DeadLineday.set(Calendar.MINUTE,0);
                    DeadLineday.set(Calendar.SECOND,0);
                    if(DeadLineday.compareTo(StartLineday)<0) {
                        tmp.setStartLine(DeadLineday.getTime().toString());
                        tmp.setDeadLine(StartLineday.getTime().toString());
                        freeTimeTable.add(tmp);
                    }
                }
            }
            else{
                tmp.setStartLine(DeadLineday.getTime().toString());
                tmp.setDeadLine(StartLineday.getTime().toString());
                freeTimeTable.add(tmp);
            }
            //待补充


        }
        return freeTimeTable;
    }

    public void InsertTaskIntoTodoList(List<Route> freeTimeTable,List<Task> taskDatas,List<Route> todoList) throws ParseException {
        //核心部分：待办事务更改策略
        //基本步骤如下：
        //1，从空闲时间序列中找到满足要求的时间段
        //2，进行约束条件的分析,见 LimitCondition函数
        //3，更新空闲时间序列和待办事务时间序列,见InsertTaskToFreeTimetable函数
        for(int i=0;i<freeTimeTable.size();){
            Calendar StartLine=Calendar.getInstance();
            StartLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).startLine);
            Calendar DeadLine=Calendar.getInstance();
            DeadLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).deadLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            //待办事务已清空的情况
            if(taskDatas.isEmpty())
                break;
            for (Task task:taskDatas) {
                Calendar TaskStartLine=Calendar.getInstance();
                TaskStartLine=task.ChangeStringToCalendar(task.startLine);
                Calendar TaskDeadLine=Calendar.getInstance();
                TaskDeadLine=task.ChangeStringToCalendar(task.deadLine);
                //基本约束条件分析
                if(TimeLimits(TaskStartLine,TaskDeadLine,StartLine)){
                    //仅作演示用，时间允许的情况下
                    if(minutes>=task.executeTime){
                        //InsertTaskToFreeTimetable，有待具体实现
                        //具体事务的开展时间、结束时间更新
                        task.startLine=StartLine.getTime().toString();
                        //执行时间小于空闲时间间隔的情况，需对空闲时间序列进行数据更新
                        if(minutes>task.executeTime){
                            StartLine.add(Calendar.MINUTE,task.executeTime);
                            task.deadLine=StartLine.getTime().toString();
                            //空闲时间拆分，移除已占用的时间段，插入更新的时间段
                            Route tmp=new Route();
                            tmp.startLine=task.deadLine;
                            tmp.deadLine=DeadLine.getTime().toString();
                            freeTimeTable.remove(freeTimeTable.get(i));
                            freeTimeTable.add(i,tmp);
                        }
                        //执行时间恰好等于空闲时间间隔的情况
                        else{
                            task.deadLine=DeadLine.getTime().toString();
                            freeTimeTable.remove(freeTimeTable.get(i));
                        }
                        todoList.add(task);
                        taskDatas.remove(task);
                        break;
                    }
                }
                //遍历待办事务序列后，未找到可用的空闲时间段，则下标i自加1
                if(task==taskDatas.get(taskDatas.size()-1)){
                    i++;
                    break;
                }
            }
        }
    }
    public boolean TimeLimits(Calendar TaskStartLine,Calendar TaskDeadLine,Calendar StartLine){
        //约束条件1，开始时间应早于空闲时间的起始时间点，截止时间应晚于空闲时间的起始时间点；
        if(TaskStartLine.compareTo(StartLine)>0){
            //事务暂未开始
            return false;
        }
        //事务已开始,分析截止时间
        if(TaskDeadLine.compareTo(StartLine)<0){
            //事务已结束
            return false;
        }
        return true;

    }
    public void InsertTaskToFreeTimetable(){
        //相应的策略，核心功能分析与实现
    }
    public void EstimateTheEffect(List<Route> todoList) throws ParseException {
        int time=0;
        for (int i=0;i<todoList.size()-1;i++) {
            //初稿，仅作演示
            Calendar StartLine=Calendar.getInstance();
            StartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
            Calendar DeadLine=Calendar.getInstance();
            DeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            time+=minutes;
        }
        //跨天情况模拟,23:00-7:00
        time-=960;
        System.out.println("The total free time is "+time+",the percent is "+(float)time*100/2880+"%.");

    }
}
