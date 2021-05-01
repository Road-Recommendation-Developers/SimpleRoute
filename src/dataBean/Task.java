package dataBean;

public class Task extends Route{
    public Task(String lineStr){
        String str[] = lineStr.split(",");
        this.endPoint=str[0];
        this.endLat=str[1];
        this.endLog=str[2];
        this.startLine=str[3];
        this.deadLine=str[4];
        this.executeTime=Integer.parseInt(str[5]);
        this.type=str[6];
    }
}
