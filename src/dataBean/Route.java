package dataBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Route implements Cloneable{
    public String startPoint;
    public String startLat;
    public String startLog;
    public String endPoint;
    public String endLat;
    public String endLog;
    public String startLine;
    public String deadLine;
    public int executeTime;
    public String type;
    public String introduction;
    public boolean isPicked;
    public Route(){

    }
    public Route(String lineStr){
        String str[] = lineStr.split(",");
        //对已有数据，通过换行符进行拆分
        this.startPoint=str[0];
        this.startLat = str[1];
        this.startLog = str[2];
        this.endPoint=str[3];
        this.endLat = str[4];
        this.endLog = str[5];
        this.startLine = str[6];
        this.executeTime =Integer.parseInt(str[7]);
        this.type=str[8];
    }


    @Override
    public String toString(){
        return
                startPoint + "," +
                        startLat + "," +
                        startLog + "," +
                        endPoint + "," +
                        endLat + "," +
                        endLog + "," +
                        startLine+"," +
                        deadLine+"," +
                        executeTime+"," +
                        type+"," +
                        introduction+"," +
                        isPicked;
    }

    public Calendar ChangeStringToCalendar(String line) throws ParseException {
        Calendar time=Calendar.getInstance();
        // 设置传入的时间格式
        //SimpleDateFormat dateFormat = new SimpleDateFormat("E MM dd HH:mm:ss yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        Date date = dateFormat.parse(line);
        // 指定一个日期
        //Date date = dateFormat.parse("Apr 12 08:00:00 2021");
        //获取时间
        //时间拓展
        time.setTime(date);
        //String tmp=time.getTime().toString();
        return time;
    }

    //允许进行数据的克隆，便于通过数据拷贝实现不同的功能
    @Override
    public Object clone() {
        Route route = null;
        try{
            route = (Route) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return route;
    }
    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getStartLat() { return startLat; }

    public void setStartLat(String startLat) { this.startLat = startLat; }

    public String getStartLog() { return startLog; }

    public void setStartLog(String startLog) { this.startLog = startLog; }

    public String getEndLat() { return endLat; }

    public void setEndLat(String endLat) { this.endLat = endLat; }

    public String getEndLog() { return endLog; }

    public void setEndLog(String endLog) { this.endLog = endLog; }

    public String getStartLine() { return startLine; }

    public void setStartLine(String startLine) { this.startLine = startLine; }

    public String getDeadLine() { return deadLine; }

    public void setDeadLine(String deadLine) { this.deadLine = deadLine; }

    public int getExecuteTime() { return executeTime; }

    public void setExecuteTime(int executeTime) { this.executeTime = executeTime; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getIntroduction() { return introduction; }

    public void setIntroduction(String introduction) { this.introduction = introduction; }

    public boolean isPicked() { return isPicked; }

    public void setPicked(boolean picked) { isPicked = picked; }

}
