package Utils;


public class Interfaces {

    public interface LoginListener {
        public void onSuccessToLogin(String response);

        public void onFailedToLogin();
    }

    public interface ContinueClickListener{
        public void onContinueClick(int day);
    }

}
