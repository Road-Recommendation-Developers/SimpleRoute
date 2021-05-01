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
        //���4.12-4.16���ʱ��εĿγ������������������������Ϊ3
        while (start.get(Calendar.DATE)<=14){
            //�ڴ��ͷ�
            todoList.clear();
            System.out.println("The course between Apr "+start.get(Calendar.DATE)+" and Apr "+end.get(Calendar.DATE)+" is:");
            //����γ�����
            for(Route r : routeDatas ) {
                Calendar tmp=Calendar.getInstance();
                tmp=r.ChangeStringToCalendar(r.getStartLine());
                //���ô��ڴ�СΪ3
                if (tmp.after(start)&& tmp.before(end)) {
                    //�ڴ����ڣ���洢��ȥ
                    todoList.add(r);
                    System.out.println(r);
                }
            }
            List<Route> freeTimeTable=GetTheFreeTimeTable(todoList);
            //�����������
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
            //��ʱ�������⣬ʱ��������
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
            //ϣ�������ڶ���ʱ�䣬��Ӧ�İ취��
            //���Ƴ���Ӧ��·�ߣ��и�ֱ����ʶ
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
            time.set(Calendar.DATE,day);//bug��Դ
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
        if(Word.equals("1-2��"))
            time="8:00-9:40";
        if(Word.equals("3-4��"))
            time="10:00-11:40";
        if(Word.equals("5-6��"))
            time="14:00-15:40";
        if(Word.equals("7-8��"))
            time="16:00-17:40";
        if(Word.equals("9-10��"))
            time="19:00-20:40";
        return time;
    }

    public int ChangeWordToIntTime(String Word){
        if(Word.equals("1-2��"))
            return 8;
        else if(Word.equals("3-4��"))
            return 10;
        else if(Word.equals("5-6��"))
            return 14;
        else if(Word.equals("7-8��"))
            return 16;
        else if(Word.equals("9-10��"))
            return 19;
        else return 0;
    }
    public int ChangeKeyToDay(String key){
        if(key.equals("һ"))
            return 12;
        else if(key.equals("��"))
            return 13;
        else if(key.equals("��"))
            return 14;
        else if(key.equals("��"))
            return 15;
        else if(key.equals("��"))
            return 16;
        else return 0;
        //��������Ĵ���������Կ��ǵ�
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
            File file = new File("src\\Data\\output.txt"); // ���·�������û����Ҫ����һ���µ�output.txt�ļ�
            FileOutputStream fos = null;
            if(!file.exists()){
                file.createNewFile();//����ļ������ڣ��ʹ������ļ�
                fos = new FileOutputStream(file);//�״�д���ȡ
            }else{
                //����ļ��Ѵ��ڣ���ô�����ļ�ĩβ׷��д��
                fos = new FileOutputStream(file,true);//���ﹹ�췽������һ������true,��ʾ���ļ�ĩβ׷��д��
            }
            OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");//ָ����GBK��ʽд���ļ�
            for(Route r:formatList) {
                osw.append(String.valueOf(r)).append("\r\n"); // \r\n��Ϊ����
                osw.flush(); // �ѻ���������ѹ���ļ�
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Route> Conclude(List<Route> routeDatas){
        //����Ŀ�꣺���漰�ĵص���й��࣬���������List
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
        //���룺����γ�����
        //���������ʱ������
        List<Route> freeTimeTable=new ArrayList<>();
        for(int i=0;i<todoList.size()-1;i++){
            Route tmp=new Route();
            tmp.type="free";
            Calendar DeadLineday=Calendar.getInstance();
            DeadLineday=tmp.ChangeStringToCalendar(todoList.get(i).getDeadLine());
            Calendar StartLineday=Calendar.getInstance();
            StartLineday=tmp.ChangeStringToCalendar(todoList.get(i+1).getStartLine());
            if(DeadLineday.get(Calendar.DATE)<StartLineday.get(Calendar.DATE)){
                //��Ϊ��ͬ�����죬Ӧ����Լ��������
                //Լ������2��23:00-7:00֮�䲻�ܰ�������
                tmp.setStartLine(DeadLineday.getTime().toString());
                DeadLineday.set(Calendar.HOUR_OF_DAY,23);
                DeadLineday.set(Calendar.MINUTE,0);
                DeadLineday.set(Calendar.SECOND,0);
                tmp.setDeadLine(DeadLineday.getTime().toString());
                freeTimeTable.add(tmp);
                //���ǵڶ���Ĵ����������ʼʱ�䲻��7��00
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
            //������


        }
        return freeTimeTable;
    }

    public void InsertTaskIntoTodoList(List<Route> freeTimeTable,List<Task> taskDatas,List<Route> todoList) throws ParseException {
        //���Ĳ��֣�����������Ĳ���
        //�����������£�
        //1���ӿ���ʱ���������ҵ�����Ҫ���ʱ���
        //2������Լ�������ķ���,�� LimitCondition����
        //3�����¿���ʱ�����кʹ�������ʱ������,��InsertTaskToFreeTimetable����
        for(int i=0;i<freeTimeTable.size();){
            Calendar StartLine=Calendar.getInstance();
            StartLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).startLine);
            Calendar DeadLine=Calendar.getInstance();
            DeadLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).deadLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            //������������յ����
            if(taskDatas.isEmpty())
                break;
            for (Task task:taskDatas) {
                Calendar TaskStartLine=Calendar.getInstance();
                TaskStartLine=task.ChangeStringToCalendar(task.startLine);
                Calendar TaskDeadLine=Calendar.getInstance();
                TaskDeadLine=task.ChangeStringToCalendar(task.deadLine);
                //����Լ����������
                if(TimeLimits(TaskStartLine,TaskDeadLine,StartLine)){
                    //������ʾ�ã�ʱ������������
                    if(minutes>=task.executeTime){
                        //InsertTaskToFreeTimetable���д�����ʵ��
                        //��������Ŀ�չʱ�䡢����ʱ�����
                        task.startLine=StartLine.getTime().toString();
                        //ִ��ʱ��С�ڿ���ʱ�������������Կ���ʱ�����н������ݸ���
                        if(minutes>task.executeTime){
                            StartLine.add(Calendar.MINUTE,task.executeTime);
                            task.deadLine=StartLine.getTime().toString();
                            //����ʱ���֣��Ƴ���ռ�õ�ʱ��Σ�������µ�ʱ���
                            Route tmp=new Route();
                            tmp.startLine=task.deadLine;
                            tmp.deadLine=DeadLine.getTime().toString();
                            freeTimeTable.remove(freeTimeTable.get(i));
                            freeTimeTable.add(i,tmp);
                        }
                        //ִ��ʱ��ǡ�õ��ڿ���ʱ���������
                        else{
                            task.deadLine=DeadLine.getTime().toString();
                            freeTimeTable.remove(freeTimeTable.get(i));
                        }
                        todoList.add(task);
                        taskDatas.remove(task);
                        break;
                    }
                }
                //���������������к�δ�ҵ����õĿ���ʱ��Σ����±�i�Լ�1
                if(task==taskDatas.get(taskDatas.size()-1)){
                    i++;
                    break;
                }
            }
        }
    }
    public boolean TimeLimits(Calendar TaskStartLine,Calendar TaskDeadLine,Calendar StartLine){
        //Լ������1����ʼʱ��Ӧ���ڿ���ʱ�����ʼʱ��㣬��ֹʱ��Ӧ���ڿ���ʱ�����ʼʱ��㣻
        if(TaskStartLine.compareTo(StartLine)>0){
            //������δ��ʼ
            return false;
        }
        //�����ѿ�ʼ,������ֹʱ��
        if(TaskDeadLine.compareTo(StartLine)<0){
            //�����ѽ���
            return false;
        }
        return true;

    }
    public void InsertTaskToFreeTimetable(){
        //��Ӧ�Ĳ��ԣ����Ĺ��ܷ�����ʵ��
    }
    public void EstimateTheEffect(List<Route> todoList) throws ParseException {
        int time=0;
        for (int i=0;i<todoList.size()-1;i++) {
            //���壬������ʾ
            Calendar StartLine=Calendar.getInstance();
            StartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
            Calendar DeadLine=Calendar.getInstance();
            DeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            time+=minutes;
        }
        //�������ģ��,23:00-7:00
        time-=960;
        System.out.println("The total free time is "+time+",the percent is "+(float)time*100/2880+"%.");

    }
}
