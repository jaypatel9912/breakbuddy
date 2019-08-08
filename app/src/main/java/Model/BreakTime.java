package Model;

public class BreakTime {
    String weekDayID;
    String startTime;
    String endTime;
    String scheduledId;
    String status;
    boolean isEnable;

    public String getWeekDayID() {
        return weekDayID;
    }

    public void setWeekDayID(String weekDayID) {
        this.weekDayID = weekDayID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScheduledId() {
        return scheduledId;
    }

    public void setScheduledId(String scheduledId) {
        this.scheduledId = scheduledId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
