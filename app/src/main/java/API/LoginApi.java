package API;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import Utils.Constants;
import Utils.Utilz;
import Utils.Interfaces;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class LoginApi {

    Interfaces.LoginListener listener;

    public LoginApi(Context context, String email, String password, Interfaces.LoginListener listener) {
        this.listener = listener;

        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject objMain = new JSONObject();
        try {
            objMain.put(Constants.MM_email, email);
            objMain.put(Constants.MM_password, password);
//            Log.i("Login API", Constants.Base_url + Constants.API_LOGIN);
            StringEntity entity = new StringEntity(objMain.toString());
            client.post(context, Constants.Base_url + Constants.API_LOGIN, entity, "application/json", new LoginHandler());
        } catch (Exception e) {
            e.printStackTrace();
            Utilz.closeProgressDialog();
        }
    }

    private class LoginHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            listener.onSuccessToLogin(new String(responseBody));
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            listener.onFailedToLogin();
        }
    }
}
