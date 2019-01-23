package com.innowave;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innowave.Adapter.AvatarListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class DetailsScreen extends AppCompatActivity {


    TextView userDetails;
    String TAG = "DetailsScreen";
    ProgressDialog dialog;
    ImageView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        avatar = (ImageView) findViewById(R.id.avatar);

        userDetails = (TextView) findViewById(R.id.details);
        userDetails.setText(getIntent().getStringExtra("userDetails"));

        new Thread(new Runnable() {
            public void run() {
                // a potentially time consuming task
                final Bitmap bitmap = getBitmapFromURL(getIntent().getStringExtra("avatar_url"));
                avatar.post(new Runnable() {
                    public void run() {
                        avatar.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();


        getUserFollowers();

        dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

    }


    public void getUserFollowers(){
        String url = getIntent().getStringExtra("followers_url");
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {



                    JSONArray jsonArray = new JSONArray(response);

                    final ArrayList<String> followerUserName = new ArrayList<>();
                    final ArrayList<String> followersAvatarUrl = new ArrayList<>();
                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject readData = jsonArray.getJSONObject(i);
                        followerUserName.add("Username: "+readData.get("login"));
                        followersAvatarUrl.add(readData.getString("avatar_url"));
                    }

                    final ArrayList<Bitmap> followersAvatar = new ArrayList<>();
                    new Thread(new Runnable() {
                        public void run() {
                           for (int i=0; i<followersAvatarUrl.size(); i++){
                               followersAvatar.add(getBitmapFromURL(followersAvatarUrl.get(i)));
                           }

                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   ListView listView = (ListView) findViewById(R.id.list);

                                   AvatarListAdapter adapter=new AvatarListAdapter(DetailsScreen.this, followerUserName, followersAvatar);

                                   listView.setAdapter(adapter);
                                   dialog.dismiss();

                               }
                           });
                        }
                    }).start();


                    if (jsonArray.length() <=0){
                        Toast.makeText(DetailsScreen.this, " No followers found", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }



            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG+" Response", ""+error);

            }



        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);


            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
