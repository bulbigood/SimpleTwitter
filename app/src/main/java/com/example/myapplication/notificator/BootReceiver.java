package com.example.myapplication.notificator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.myapplication.NetworkLoader;
import com.example.myapplication.Utils;
import com.example.myapplication.api.AppInitializer;
import com.example.myapplication.api.BearerToken;
import com.example.myapplication.api.URLEncoder;
import com.example.myapplication.api.structure.Tweet;
import com.example.myapplication.page.PageController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    private static BearerToken token = null;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String authCode = "Basic " + URLEncoder.getBearerTokenCredentials(NetworkLoader.CONSUMER_KEY, NetworkLoader.CONSUMER_SECRET);
        AppInitializer.getApi().getBearerToken("client_credentials", authCode).enqueue(new CallbackToken());
    }

    private class CallbackToken implements Callback<BearerToken> {

        @Override
        public void onResponse(Call<BearerToken> call, Response<BearerToken> response) {
            token = response.body();
            if (token == null)
                Log.e("API_ERROR", response.message());
            else {
                AppInitializer.getApi().getTimeline(PageController.HOME_PAGE, 1, null, null, token.getRequestToken())
                        .enqueue(new CallbackNotification());
            }
        }

        @Override
        public void onFailure(Call<BearerToken> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }

    private class CallbackNotification implements Callback<List<Tweet>> {

        @Override
        public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
            Tweet body = response.body().get(0);

            if (body == null)
                Log.e("API_ERROR", response.message());
            else {
                Long savedID = Utils.getIDfromFile(context);
                Long loadedID = body.getId();
                if (!loadedID.equals(savedID)) {
                    NotificationHandler.getInstance(context).createExpandableNotification(context, body);
                    Utils.writeIDtoFile(context, loadedID);
                }
            }
        }

        @Override
        public void onFailure(Call<List<Tweet>> call, Throwable t) {
            Log.e("API_CONNECTION_ERROR", t.getMessage());
        }
    }
}
