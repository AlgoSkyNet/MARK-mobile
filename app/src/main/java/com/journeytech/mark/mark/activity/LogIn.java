package com.journeytech.mark.mark.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.journeytech.mark.mark.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class LogIn extends Activity {

    String usernameValue, passwordValue;
    String baseUrl ="http://mark.journeytech.com.ph/mobile_api/";

    NetworkAPI networkAPI;

    public interface NetworkAPI {
        @POST("authentication.php")
        @Headers({"Content-Type:application/json; charset=UTF-8"})
        Call<JsonElement> loginRequest(@Body LoginRequest body);
    }
    public class LoginRequest {
        String userid;
        String pass;
        public LoginRequest(String userid, String pass) {
            this.userid = userid;
            this.pass = pass;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        final EditText usr =  (EditText) findViewById(R.id.username);

        final EditText pw =  (EditText) findViewById(R.id.password);


        Button b = (Button) findViewById(R.id.loginButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameValue = usr.getText().toString();
                passwordValue = pw.getText().toString();
//                Toast.makeText(getApplicationContext(), usernameValue + passwordValue , Toast.LENGTH_SHORT).show();
                if(usernameValue.isEmpty()||passwordValue.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Check Your Credentials..", Toast.LENGTH_SHORT).show();
                } else {
                    loginRequest_validate(usernameValue, passwordValue);
                }
            }
        });

    }

    public void loginRequest_validate(String username, String password){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        networkAPI = retrofit.create(NetworkAPI.class);

        LoginRequest loginRequest = new LoginRequest(username, password);

        Call<JsonElement> call = networkAPI.loginRequest(loginRequest);

        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                // success response
                if(response.body().isJsonObject()){
                    JsonObject objectWhichYouNeed = response.body().getAsJsonObject();
                    System.out.println(objectWhichYouNeed);
//                    if(response.body().)
                    for(int i = 0; i< response.body().getAsJsonObject().size(); i++){
                        String status = response.body().getAsJsonObject().get("status").getAsString();

                        if(status.equals("Log in Success!")){
                            String ucsi_num = response.body().getAsJsonObject().get("ucsi_num").getAsString();
                            String client_table = response.body().getAsJsonObject().get("client_table").getAsString();
                            String markutype = response.body().getAsJsonObject().get("markutype").getAsString();
                            System.out.println(status + ucsi_num + client_table + markutype);
                            Intent ii=new Intent(LogIn.this, MainActivity.class);
                            ii.putExtra("status", status);
                            ii.putExtra("ucsi_num", ucsi_num);
                            ii.putExtra("client_table", client_table);
                            ii.putExtra("markutype", markutype);
                            startActivity(ii);
                        } else {
                            Toast.makeText(getApplicationContext(), "Please check your credentials...", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your credentials...", Toast.LENGTH_SHORT).show();
                }

/*                if(response.isSuccessful()) {
                    JsonObject objectWhichYouNeed = response.body().getAsJsonObject();
                    Toast.makeText(getApplicationContext(), "Success" + objectWhichYouNeed + "              " + response.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please check your credentials...", Toast.LENGTH_SHORT).show();
                }*/

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // failure response
                Toast.makeText(getApplicationContext(), "Failure" + call.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
