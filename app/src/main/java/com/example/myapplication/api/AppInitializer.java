package com.example.myapplication.api;

import android.app.Application;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppInitializer extends Application {

    private static TwitterAPI twitterApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twitter.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        twitterApi = retrofit.create(TwitterAPI.class); //Создаем объект, при помощи которого будем выполнять запросы
    }

    public static TwitterAPI getApi() {
        return twitterApi;
    }
}
