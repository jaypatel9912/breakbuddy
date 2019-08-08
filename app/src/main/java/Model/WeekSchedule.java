package Model;

import java.util.ArrayList;

public class WeekSchedule {
    ArrayList<BreakTime> ScheduleList;
    String endTime;

    public ArrayList<BreakTime> getScheduleList() {
        return ScheduleList;
    }

    public void setScheduleList(ArrayList<BreakTime> scheduleList) {
        ScheduleList = scheduleList;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
