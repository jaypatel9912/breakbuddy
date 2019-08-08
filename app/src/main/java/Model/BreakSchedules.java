package Model;


import java.util.ArrayList;

public class BreakSchedules {

        String weekday_id;
        ArrayList<schedules> schedules = new ArrayList<>();

    public String getWeekday_id() {
        return weekday_id;
    }

    public void setWeekday_id(String weekday_id) {
        this.weekday_id = weekday_id;
    }

    public ArrayList<BreakSchedules.schedules> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<BreakSchedules.schedules> schedules) {
        this.schedules = schedules;
    }

    public class schedules{
        String from_time,to_time, status;

        public String getFrom_time() {
            return from_time;
        }

        public void setFrom_time(String from_time) {
            this.from_time = from_time;
        }

        public String getTo_time() {
            return to_time;
        }

        public void setTo_time(String to_time) {
            this.to_time = to_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }


}
