package br.com.busaonota10.service;

import android.content.Context;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import br.com.busaonota10.R;


public class BusaoService {

    private Context context;
    private final String BASE_URL;
    private OkHttpClient client = new OkHttpClient();
    public final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    public BusaoService(Context context) {
        this.context = context;
        this.BASE_URL = this.context.getResources().getString(R.string.busao_service_api);
    }

    public void get(String path, Callback callback) {
        client.newCall(buildGETRequest(path)).enqueue(callback);
    }

    public void post(String path, String json, Callback callback) {
        client.newCall(buildPOSTRequest(path, json)).enqueue(callback);
    }

    private String getAbsoluteUrl(String path) {
        return String.format("%s%s", BASE_URL, path);
    }

    private Request buildGETRequest(String path) {
        String url = getAbsoluteUrl(path);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();
        return request;
    }

    private Request buildPOSTRequest(String path, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        String url = getAbsoluteUrl(path);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .build();
        return request;
    }
}
