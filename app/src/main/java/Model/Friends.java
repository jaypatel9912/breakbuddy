package Model;


public class Friends {
    String friend_user_id;
    String from_time;
    String to_time;
    String firstname;
    String lastname;
    String email;
    String phone_no;
    String is_online_offline;
    boolean ifShowSection = false;
    String parentFromTime;
    String parentToTime;

    public String getParentFromTime() {
        return parentFromTime;
    }

    public void setParentFromTime(String parentFromTime) {
        this.parentFromTime = parentFromTime;
    }

    public String getParentToTime() {
        return parentToTime;
    }

    public void setParentToTime(String parentToTime) {
        this.parentToTime = parentToTime;
    }

    public boolean isIfShowSection() {
        return ifShowSection;
    }

    public void setIfShowSection(boolean ifShowSection) {
        this.ifShowSection = ifShowSection;
    }

    public String getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(String friend_user_id) {
        this.friend_user_id = friend_user_id;
    }

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getIs_online_offline() {
        return is_online_offline;
    }

    public void setIs_online_offline(String is_online_offline) {
        this.is_online_offline = is_online_offline;
    }
}
