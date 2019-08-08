package Model;


public class LeaveSchedules {
        String leave_schedule_id,  weekday_id, leave_time;

    public String getLeave_time() {
        return leave_time;
    }

    public void setLeave_time(String leave_time) {
        this.leave_time = leave_time;
    }

    public String getLeave_schedule_id() {
        return leave_schedule_id;
    }

    public void setLeave_schedule_id(String leave_schedule_id) {
        this.leave_schedule_id = leave_schedule_id;
    }

    public String getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(String weekday_id) {
        this.weekday_id = weekday_id;
    }
}
