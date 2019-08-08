package API;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Utils.Constants;

public class CreateCountryCodeList extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {

        try {
            JSONArray arr = new JSONArray(Constants.MM_COUNTRY_CODES);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                Constants.MM_COUNTRY_MAP.put(obj.getString(Constants.MM_Name) + " (" + obj.getString(Constants.MM_ISO) + ")", obj.getString(Constants.MM_Code));
                Constants.MM_COUNTRY_NAMES[i] = obj.getString(Constants.MM_Name) + " (" + obj.getString(Constants.MM_ISO) + ")";
            }
            Log.i("JSONArray", arr.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
