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
        List<Route> todoList=new ArrayList<>();
        //FormateTheInfos(routeDatas);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        WindowInitilize(start,end);
        //System.out.println(start.getTime().toString()+end.getTime().toString());
        //输出4.12-4.16这个时间段的课程情况与待办事务安排情况，窗口为3
        while (start.get(Calendar.DATE)<=14){
            //内存释放
            todoList.clear();
            System.out.println("The course between Apr "+start.get(Calendar.DATE)+" and Apr "+end.get(Calendar.DATE)+" is:");
            //插入课程数据
            for(Route r : routeDatas ) {
                Calendar tmp=r.ChangeStringToCalendar(r.getStartLine());
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
            //初步假定步长为1天，其作用在于应对数据的实时变化与待办事务的动态更新
            ChangeTheWindowByStep(start,end,1);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("--------------");
            //todoList.sort,以时间顺序进行排序
            todoList.sort((x, y) -> {
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
            );
            //绘制出相应的路线，有个直观认识
            DrawTheRoute(todoList);
            //质量工程，效果评估
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
        List<Route> formatList= new ArrayList<>();
        Map<String, Integer> cnt = new LinkedHashMap<>() ;

        for ( Route r : routeDatas ) {
            cnt.merge(r.getStartLine().substring(1, 2), 1, Integer::sum);
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
        return switch (Word) {
            case "1-2节" -> 8;
            case "3-4节" -> 10;
            case "5-6节" -> 14;
            case "7-8节" -> 16;
            case "9-10节" -> 19;
            default -> 0;
        };
    }
    public int ChangeKeyToDay(String key){
        return switch (key) {
            case "一" -> 12;
            case "二" -> 13;
            case "三" -> 14;
            case "四" -> 15;
            case "五" -> 16;
            default -> 0;
        };
        //意外情况的处理，还需加以考虑的
    }

    public boolean InTimeWindow(Calendar endTime,Calendar windowTime){
        return windowTime.compareTo(endTime) > 0;
    }

    public void DrawTheRoute(List<Route> routeDatas){
        String startPoint,endPoint,startLine,deadLine;
        String startLat,startLog,endLat,endLog;
        String type;
        int executeTime;
        for (Route routeData : routeDatas) {
            startPoint = routeData.getStartPoint();
            startLat = routeData.getStartLat();
            startLog = routeData.getStartLog();
            endPoint = routeData.getEndPoint();
            endLat = routeData.getEndLat();
            endLog = routeData.getEndLog();
            startLine = routeData.getStartLine();
            executeTime = routeData.getExecuteTime();
            deadLine = routeData.getDeadLine();
            type = routeData.getType();
            System.out.println("The route is from " + startPoint + " to " + endPoint + ",the start point location is:" + startLat + "," + startLog + ",the end point location is:" + endLat + "," + endLog + ",the start line is " + startLine + ",the dead line is " + deadLine + ",the execute time is " + executeTime + ",the type is " + type);

        }
    }
    public static void writeFile(List<Route> formatList) {
        try {
            File file = new File("src\\Data\\output.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            FileOutputStream fos;
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
        List<Route> orderedList= new ArrayList<>();
        Map<String, Integer> cnt = new TreeMap<>();
        for ( Route r : routeDatas ) {
            cnt.merge(r.getStartPoint(), 1, Integer::sum);
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
            Calendar DeadLineday;
            DeadLineday=tmp.ChangeStringToCalendar(todoList.get(i).getDeadLine());
            Calendar StartLineday;
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
                        Route tmp2=new  Route();
                        tmp2.setStartLine(DeadLineday.getTime().toString());
                        tmp2.setDeadLine(StartLineday.getTime().toString());
                        freeTimeTable.add(tmp2);
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
        //改进：加入距离因素，进一步分析相应的解的情况
        List<Task> tmpTask=new ArrayList<>();
        for(int i=0;i<freeTimeTable.size();){
            tmpTask.clear();
            Calendar StartLine;
            StartLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).startLine);
            Calendar DeadLine;
            DeadLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).deadLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            //待办事务已清空的情况
            if(taskDatas.isEmpty())
                break;
            for (Task task:taskDatas) {
                Calendar TaskStartLine;
                TaskStartLine=task.ChangeStringToCalendar(task.startLine);
                Calendar TaskDeadLine;
                TaskDeadLine=task.ChangeStringToCalendar(task.deadLine);
                //基本约束条件分析
                if(TimeLimits(TaskStartLine,TaskDeadLine,StartLine)){
                    //仅作演示用，时间允许的情况下
                    //考虑到距离因素，存在一对多的关系，先用集合存储，再找到局部最优解
                    if(minutes>=task.executeTime){
                        //InsertTaskToFreeTimetable，有待具体实现
                        //具体事务的开展时间、结束时间更新
                        tmpTask.add(task);
                    }
                }
                if(task==taskDatas.get(taskDatas.size()-1)&&tmpTask.isEmpty()){
                    i++;
                    break;
                }
            }
            //遍历待办事务序列后，未找到可用的空闲时间段，则下标i自加1

            double distance=0;
            for (int k=0;k<tmpTask.size();k++) {
                //综合评估：距离+时间，对应给分
                //策略问题，以FIFO作为示例
                Calendar tmpStartLine;
                tmpStartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
                Calendar tmpDeadLine;
                tmpDeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
                distance=GetDistance(todoList.get(i),todoList.get(i+1));

                tmpTask.get(k).startLine=StartLine.getTime().toString();
                //执行时间小于空闲时间间隔的情况，需对空闲时间序列进行数据更新
                if(minutes>tmpTask.get(k).executeTime){
                    StartLine.add(Calendar.MINUTE,tmpTask.get(k).executeTime);
                    tmpTask.get(k).deadLine=StartLine.getTime().toString();
                    //空闲时间拆分，移除已占用的时间段，插入更新的时间段
                    Route tmp=new Route();
                    tmp.startLine=tmpTask.get(k).deadLine;
                    tmp.deadLine=DeadLine.getTime().toString();
                    freeTimeTable.remove(freeTimeTable.get(i));
                    freeTimeTable.add(i,tmp);
                }
                //执行时间恰好等于空闲时间间隔的情况
                else{
                    tmpTask.get(k).deadLine=DeadLine.getTime().toString();
                    freeTimeTable.remove(freeTimeTable.get(i));
                }
                todoList.add(tmpTask.get(k));
                taskDatas.remove(tmpTask.get(k));
                break;
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
        double distance=0;
        for (int i=0;i<todoList.size()-1;i++) {
            //初稿，仅作演示
            Calendar StartLine;
            StartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
            Calendar DeadLine;
            DeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            time+=minutes;

            distance+=GetDistance(todoList.get(i),todoList.get(i+1));
        }
        //跨天情况模拟,23:00-7:00
        time-=960;
        System.out.println("The total free time is "+time+",the percent is "+(float)time*100/2880+"%,"+"the total distance is "+distance);

    }
    public void WindowInitilize(Calendar start,Calendar end){
        //initilize the start time
        start.set(Calendar.MONTH,3);
        start.set(Calendar.DATE,12);
        start.set(Calendar.HOUR_OF_DAY,0);
        start.set(Calendar.MINUTE,0);
        start.set(Calendar.SECOND,0);
        //initilize the end time
        end.set(Calendar.MONTH,3);
        end.set(Calendar.DATE,15);
        end.set(Calendar.HOUR_OF_DAY,0);
        end.set(Calendar.MINUTE,0);
        end.set(Calendar.SECOND,0);
    }
    public void ChangeTheWindowByStep(Calendar start,Calendar end,int step){
        //步长暂时以天为单位
        start.add(Calendar.DATE,step);
        end.add(Calendar.DATE,step);
    }

    private double GetDistance(Route r1, Route r2) {
        double PI = 3.1415926D;
        double Earth_Radius = 6371.004D;
        double radLat1 = Double.parseDouble(r1.getEndLat())* PI / 180.0D;
        double radLat2 = Double.parseDouble(r2.getEndLat()) * PI / 180.0D;
        double a = radLat1 - radLat2;
        double b = Double.parseDouble(r1.getEndLog()) * PI / 180.0D - Double.parseDouble(r2.getEndLog())* PI / 180.0D;
        //double s = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0D), 2.0D) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0D), 2.0D)));
        double s = Math.acos(Math.sin(radLat1)*Math.sin(radLat2)+Math.cos(radLat1) * Math.cos(radLat2)*Math.cos(b));
        s *= Earth_Radius;
        s *= 1000.0D;
        //D = arc cos((sin北纬A×sin北纬B)＋(cos北纬A×cos北纬B×cosAB两地经度差))×地球平均半径 (Shormin) 其中地球平均半径为6371.004 km，D的单位为km
        return s;
    }
}
