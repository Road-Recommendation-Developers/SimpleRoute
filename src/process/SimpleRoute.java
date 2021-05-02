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
        //���4.12-4.16���ʱ��εĿγ������������������������Ϊ3
        while (start.get(Calendar.DATE)<=14){
            //�ڴ��ͷ�
            todoList.clear();
            System.out.println("The course between Apr "+start.get(Calendar.DATE)+" and Apr "+end.get(Calendar.DATE)+" is:");
            //����γ�����
            for(Route r : routeDatas ) {
                Calendar tmp=r.ChangeStringToCalendar(r.getStartLine());
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
            //�����ٶ�����Ϊ1�죬����������Ӧ�����ݵ�ʵʱ�仯���������Ķ�̬����
            ChangeTheWindowByStep(start,end,1);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("--------------");
            //todoList.sort,��ʱ��˳���������
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
            //���Ƴ���Ӧ��·�ߣ��и�ֱ����ʶ
            DrawTheRoute(todoList);
            //�������̣�Ч������
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
        return switch (Word) {
            case "1-2��" -> 8;
            case "3-4��" -> 10;
            case "5-6��" -> 14;
            case "7-8��" -> 16;
            case "9-10��" -> 19;
            default -> 0;
        };
    }
    public int ChangeKeyToDay(String key){
        return switch (key) {
            case "һ" -> 12;
            case "��" -> 13;
            case "��" -> 14;
            case "��" -> 15;
            case "��" -> 16;
            default -> 0;
        };
        //��������Ĵ���������Կ��ǵ�
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
            File file = new File("src\\Data\\output.txt"); // ���·�������û����Ҫ����һ���µ�output.txt�ļ�
            FileOutputStream fos;
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
        //���룺����γ�����
        //���������ʱ������
        List<Route> freeTimeTable=new ArrayList<>();
        for(int i=0;i<todoList.size()-1;i++){
            Route tmp=new Route();
            tmp.type="free";
            Calendar DeadLineday;
            DeadLineday=tmp.ChangeStringToCalendar(todoList.get(i).getDeadLine());
            Calendar StartLineday;
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
        //�Ľ�������������أ���һ��������Ӧ�Ľ�����
        List<Task> tmpTask=new ArrayList<>();
        for(int i=0;i<freeTimeTable.size();){
            tmpTask.clear();
            Calendar StartLine;
            StartLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).startLine);
            Calendar DeadLine;
            DeadLine=freeTimeTable.get(i).ChangeStringToCalendar(freeTimeTable.get(i).deadLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            //������������յ����
            if(taskDatas.isEmpty())
                break;
            for (Task task:taskDatas) {
                Calendar TaskStartLine;
                TaskStartLine=task.ChangeStringToCalendar(task.startLine);
                Calendar TaskDeadLine;
                TaskDeadLine=task.ChangeStringToCalendar(task.deadLine);
                //����Լ����������
                if(TimeLimits(TaskStartLine,TaskDeadLine,StartLine)){
                    //������ʾ�ã�ʱ������������
                    //���ǵ��������أ�����һ�Զ�Ĺ�ϵ�����ü��ϴ洢�����ҵ��ֲ����Ž�
                    if(minutes>=task.executeTime){
                        //InsertTaskToFreeTimetable���д�����ʵ��
                        //��������Ŀ�չʱ�䡢����ʱ�����
                        tmpTask.add(task);
                    }
                }
                if(task==taskDatas.get(taskDatas.size()-1)&&tmpTask.isEmpty()){
                    i++;
                    break;
                }
            }
            //���������������к�δ�ҵ����õĿ���ʱ��Σ����±�i�Լ�1

            double distance=0;
            for (int k=0;k<tmpTask.size();k++) {
                //�ۺ�����������+ʱ�䣬��Ӧ����
                //�������⣬��FIFO��Ϊʾ��
                Calendar tmpStartLine;
                tmpStartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
                Calendar tmpDeadLine;
                tmpDeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
                distance=GetDistance(todoList.get(i),todoList.get(i+1));

                tmpTask.get(k).startLine=StartLine.getTime().toString();
                //ִ��ʱ��С�ڿ���ʱ�������������Կ���ʱ�����н������ݸ���
                if(minutes>tmpTask.get(k).executeTime){
                    StartLine.add(Calendar.MINUTE,tmpTask.get(k).executeTime);
                    tmpTask.get(k).deadLine=StartLine.getTime().toString();
                    //����ʱ���֣��Ƴ���ռ�õ�ʱ��Σ�������µ�ʱ���
                    Route tmp=new Route();
                    tmp.startLine=tmpTask.get(k).deadLine;
                    tmp.deadLine=DeadLine.getTime().toString();
                    freeTimeTable.remove(freeTimeTable.get(i));
                    freeTimeTable.add(i,tmp);
                }
                //ִ��ʱ��ǡ�õ��ڿ���ʱ���������
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
        double distance=0;
        for (int i=0;i<todoList.size()-1;i++) {
            //���壬������ʾ
            Calendar StartLine;
            StartLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i).deadLine);
            Calendar DeadLine;
            DeadLine=todoList.get(i).ChangeStringToCalendar(todoList.get(i+1).startLine);
            long difference=DeadLine.getTimeInMillis()-StartLine.getTimeInMillis();
            int minutes=(int)difference/(60*1000);
            time+=minutes;

            distance+=GetDistance(todoList.get(i),todoList.get(i+1));
        }
        //�������ģ��,23:00-7:00
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
        //������ʱ����Ϊ��λ
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
        //D = arc cos((sin��γA��sin��γB)��(cos��γA��cos��γB��cosAB���ؾ��Ȳ�))������ƽ���뾶 (Shormin) ���е���ƽ���뾶Ϊ6371.004 km��D�ĵ�λΪkm
        return s;
    }
}
