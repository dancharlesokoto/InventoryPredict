package com.thesis.inventory;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SeverActivity extends AppCompatActivity {

    private TextView resultTextView, prodname, prodquan, ptreddate, prodes;
    private static final String TAG = SeverActivity.class.getSimpleName();
    String dId, dTitle, dDes, dBar, dQuantity, dPrice, dProno;

    DatePicker picker;
    Button btnGet;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sever);

        resultTextView = findViewById(R.id.resultTextView);
        prodname = findViewById(R.id.item);
        prodquan = findViewById(R.id.stock);
        prodes = findViewById(R.id.itemdes);

        picker=(DatePicker)findViewById(R.id.datePicker1);
        btnGet=(Button)findViewById(R.id.btnpredict);

        Bundle bundle = getIntent().getExtras();
        //Intent intent = getIntent();
        //final String temp = intent.getStringExtra("url");

        dId = bundle.getString("zId");
        dTitle = bundle.getString("zTitle");
        dDes = bundle.getString("zDes");
        dBar = bundle.getString("zBar");
        dQuantity = bundle.getString("zQuantity");
        dPrice = bundle.getString("zPrice");

        dProno = bundle.getString("zProno");

        prodname.setText(dTitle);
        prodquan.setText(dQuantity);
        prodes.setText(dDes);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String a="1",b=dProno,c=String.valueOf(picker.getMonth()),d= String.valueOf(picker.getDayOfMonth()), e=String.valueOf(picker.getYear());

                List<String> list = Arrays.asList(a,b,c,d,e);

                String result = String.join(",",list);

                String setdata = "[[" + result +"]]";


                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, setdata);
                Request request = new  Request.Builder()
                        .url("https://foreweb.herokuapp.com/predict")
                        .method("POST", body)
                        .addHeader("Content-Type","application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        if (response.isSuccessful()){
                            final String myres = response.body().string();

                            SeverActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText(myres);
                                }
                            });
                        }
                    }
                });

            }
        });

    }



}
