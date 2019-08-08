package Utils;


import java.util.ArrayList;
import java.util.HashMap;

import Model.Contact;

public class  Constants {

    public static final String APP_PACKAGE_NAME = "com.breakbuddy";

    public static final String MM_STATUS_CODE = "statuscode";

    public static final String INVITE_TEXT = "Hey! I am using BreakBuddy. A free phone application to catch all of your friends' breaks, all in one place. Add me! \n\niOS:  https://itunes.apple.com/us/app/breakbuddy/id1191169954?ls=1&mt=8 \n\n Android:  https://play.google.com/store/apps/details?id=com.breakbuddy";

    public static final String MM_STATUS_CODE_SUCCESS = "200";
    public static final String MM_STATUS_CODE_PHONE_NOT_VERIFIED = "400";


    // api names
    public static final String API_SIGNUP = "signup";
    public static final String API_LOGIN = "login";
    public static final String API_RESEND_LINK = "resend_verfication_link";
    public static final String API_FORGOT_PASSWORD = "forget_password";
    public static final String API_change_password = "change_password";
    public static final String API_update_user_email = "update_user_email";
    public static final String API_get_break_schedule = "get_break_schedule";
    public static final String API_get_weekdaywise_break_schedule_v3 = "get_weekdaywise_break_schedule_v3";
    public static final String API_added_me_friend_list = "added_me_friend_list";
    public static final String API_add_friends_manually = "add_friends_manually";
    public static final String API_find_breakbuddy_friends = "find_breakbuddy_friends";
    public static final String API_delete_friend = "delete_friend";
    public static final String API_get_currently_on_break_friends = "get_currently_on_break_friends";
    public static final String API_send_friend_request = "send_friend_request";
    public static final String API_accept_friend_request = "accept_friend_request";
    public static final String API_reject_friend_request = "reject_friend_request";
    public static final String API_update_break_schedule = "update_break_schedule";
    public static final String API_my_friend_list = "my_friend_list";
    public static final String API_show_hide_my_break_schedule = "show_hide_my_break_schedule";
    public static final String API_reset_password = "reset_password";
    public static final String API_terms_of_use = "terms_of_use";
    public static final String API_resend_otp = "resend_otp";
    public static final String API_verify_phone_no = "verify_phone_no";
    public static final String API_verify_account = "verify_account";

    public static final String MM_email = "email";
    public static final String MM_password = "password";
    public static final String MM_old_password = "old_password";
    public static final String MM_firstname = "firstname";
    public static final String MM_lastname = "lastname";
    public static final String MM_user_id = "user_id";
    public static final String MM_phone_no = "phone_no";
    public static final String MM_is_active = "is_active";
    public static final String MM_USER_DETAILS = "USER_DETAILS";
    public static final String MM_verification_token = "verification_token";

    public static final String MM_is_new_register = "is_new_register";

    public static final String MM_msg = "msg";
    public static final String MM_user = "user";
    public static final String MM_days = "days";
    public static final String MM_day_id = "day_id";
    public static final String MM_added_me_count = "added_me_count";
    public static final String MM_showActivationDialog = "showActivationDialog";
    public static final String MM_Schedule_Day_id = "Schedule_Day_id";
    public static final String MM_week_schedeule = "week_schedeule";
    public static final String MM_weekday_id = "weekday_id";
    public static final String MM_current_weekday_id = "current_weekday_id";

    public static final String MM_friends = "friends";
    public static final String MM_status = "status";
    public static final String MM_friend_user_id = "friend_user_id";
    public static final String MM_users = "users";
    public static final String MM_unregistered = "unregistered";
    public static final String MM_registered = "registered";

    public static final String MM_break_schedules = "break_schedules";
    public static final String MM_schedules = "schedules";
    public static final String MM_from_time = "from_time";
    public static final String MM_to_time = "to_time";

    public static final String MM_leave_schedules = "leave_schedules";
    public static final String MM_leave_schedule_id = "leave_schedule_id";
    public static final String MM_leave_time = "leave_time";
    public static final String MM_current_time = "current_time";

    public static final String MM_friend_name = "friend_name";

    public static final String MM_is_show_hide = "is_show_hide";
    public static final String MM_is_online_offline = "is_online_offline";

    public static final String MM_reset_password_token = "reset_password_token";

    public static ArrayList<Contact> contacts = new ArrayList<>();

    public static ArrayList<String> emails = new ArrayList<>();

    public static ArrayList<String> phoneNumbers = new ArrayList<>();

    public static ArrayList<String> ContactNames = new ArrayList<>();

    public static final String MM_STATUS_CODE_300 = "300";
    public static final String MM_STATUS_CODE_301 = "301";

    public static final String MM_Name = "Name";
    public static final String MM_ISO = "ISO";
    public static final String MM_Code = "Code";
    public static final String MM_otp = "otp";

    public static final HashMap<String, String> MM_COUNTRY_MAP = new HashMap<>();
    public static final CharSequence[] MM_COUNTRY_NAMES = new CharSequence[2];

    public static final String MM_COUNTRY_CODES = "[\n" +
            "  {\n" +
            "    \"Name\": \"Canada\",\n" +
            "    \"ISO\": \"ca\",\n" +
            "    \"Code\": \"1\"\n" +
            "  },\n" +

            "  {\n" +
            "    \"Name\": \"United States\",\n" +
            "    \"ISO\": \"us\",\n" +
            "    \"Code\": \"1\"\n" +
            "  }\n" +
            "]";

}
