package com.innowave;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchScreen extends AppCompatActivity implements View.OnClickListener {

    String TAG = "SearchScreen";
    Button searchBtn;
    EditText searchBox;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchscreen);
        initUi();
    }

    void initUi(){

        searchBox = (EditText) findViewById(R.id.searchbox);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // Perform action on click
        switch(v.getId()) {

            case R.id.searchbox:
                break;

            case R.id.searchBtn:
                if (isNetworkConnected()){
                    dialog = ProgressDialog.show(this, "",
                            "Loading. Please wait...", true);
                    getUserData();
                }

                else
                    AlertDialogue(SearchScreen.this, "No Internet", "Connect " +
                            "to Internet before querying data");
                break;
        }
    }

    public void getUserData(){
        String url = "https://api.github.com/users/"+searchBox.getText().toString().trim() ;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.i(TAG, ""+jsonObject.getString("name"));
                    Log.i(TAG, ""+jsonObject.getString("avatar_url"));
                    Log.i(TAG, ""+jsonObject.getString("email"));
                    Log.i(TAG, ""+jsonObject.get("followers_url"));


                    String dataToPassToNextScreen = "User Name: "+ jsonObject.getString("name")+
                            "\nEmail:" +jsonObject.getString("email");


                    Intent intentToDetailsActivity = new Intent(SearchScreen.this, DetailsScreen.class);
                    intentToDetailsActivity.putExtra("userDetails", dataToPassToNextScreen);
                    intentToDetailsActivity.putExtra("avatar_url", jsonObject.getString("avatar_url"));
                    intentToDetailsActivity.putExtra("followers_url", jsonObject.getString("followers_url"));
                    startActivity(intentToDetailsActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.i(TAG, ""+jsonObject.getString("message"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    AlertDialogue(SearchScreen.this, "User not found", "User not found in github");
                }

                dialog.dismiss();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG+" Response", ""+error);
                AlertDialogue(SearchScreen.this, "User not found", "User not found in github");
            }



        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    void AlertDialogue(Context context, String title, String message){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
