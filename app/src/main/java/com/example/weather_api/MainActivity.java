package com.example.weather_api;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        EditText cityName;
        LinearLayout scroll;
        public void weather(View view){
            Log.i("City Name",cityName.getText().toString());
            DownloadTask task=new DownloadTask();
            String Cityname= cityName.getText().toString();
//                "lat=35&lon=139" this is for latitide and longitude
//            task.execute("https://api.openweathermap.org/data/2.5/forecast?"+CityName+"&appid=335616d40aaff9af37be82910b223f2d");


            //any city
            task.execute("https://api.openweathermap.org/data/2.5/forecast?q="+Cityname+",IN&mode=JSON&appid=335616d40aaff9af37be82910b223f2d");
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        cityName=(EditText)findViewById(R.id.cityName);
        scroll=(LinearLayout)findViewById(R.id.scroll);
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try {
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection)url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char curr=(char)data;
                    result+=curr;
                    data=reader.read();
                }
                return  result;
            } catch (MalformedURLException e) {
                Log.i("Wron city","Wrong name");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result!=null) {
                Log.i("Result:", result);
                try {
                    String message = "";
                    JSONObject jsonObject = new JSONObject((result));
                    //first we extract city information from JSOn data
                    JSONObject cityInfo = jsonObject.getJSONObject("city");
                    Log.i("msg", "=========city information==========");
                    Log.i("City Name", cityInfo.getString("name").toString());
                    Log.i("Country Name", cityInfo.getString("country").toString());
                    Log.i("msg", "=========ended==========");

                    Locale l = new Locale("", cityInfo.getString("country").toString());
                    String tmp = "City: " + cityInfo.getString("name").toString() + ",\r\n" + "Country:" + l.getDisplayCountry() + "\r\n";
                    JSONArray list = jsonObject.getJSONArray("list");

                    //Delete all view of scroll

                    if(((LinearLayout) scroll).getChildCount() > 0)
                        ((LinearLayout) scroll).removeAllViews();

//                list.length()
                    for (int i = 0; i < list.length(); i++) {
                        try {

                            //for each prediction
                            LinearLayout mainBox = new LinearLayout(MainActivity.this);
                            mainBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            mainBox.setOrientation(LinearLayout.HORIZONTAL);
                            mainBox.setBackgroundColor(Color.parseColor("#2F8BD5"));
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mainBox.getLayoutParams();
                            params.bottomMargin = 5;

                            Animation fadeIn = new AlphaAnimation(0, 1);
                            fadeIn.setDuration(1000);
                            Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeOut.setStartOffset(1000);
                            fadeOut.setDuration(1000);
                            AnimationSet animation = new AnimationSet(true);
                            animation.addAnimation(fadeIn);
                            animation.addAnimation(fadeOut);
                            mainBox.startAnimation(animation);



                            scroll.addView(mainBox);

                            //now add two vertical linear layout in main box
                            //one for tex&& other for tex and image
                            LinearLayout left = new LinearLayout(MainActivity.this);
                            left.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            left.setPadding(50, 25, 50, 25);
                            left.setOrientation(LinearLayout.VERTICAL);
                            // add text fields in left box (like temerature, speed etc)

                            //create five TextView and add them to the left box
                            TextView textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            JSONObject periodically = list.getJSONObject(i);
                            textView.setText(periodically.getString("dt_txt"));
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            left.addView(textView);


                            JSONArray weatherArray = periodically.getJSONArray("weather");
                            JSONObject weatherObject = weatherArray.getJSONObject(0);
                            textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView.setText(weatherObject.getString("main") + ": " + weatherObject.getString("description"));
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            left.addView(textView);


                            JSONObject wind = periodically.getJSONObject("wind");
                            textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView.setText("Wind: " + wind.getString("speed") + " m/s");
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            left.addView(textView);


                            JSONObject main = periodically.getJSONObject("main");

                            textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView.setText("Pressure: " + main.getString("pressure") + " hPa");
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            left.addView(textView);

                            textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView.setText("Humidity: " + main.getString("humidity") + "1%");
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            left.addView(textView);

                            mainBox.addView(left);

                            //now create right box
                            LinearLayout right = new LinearLayout(MainActivity.this);
                            right.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            right.setOrientation(LinearLayout.VERTICAL);


                            //add clould image in right box
                            ImageView image = new ImageView(MainActivity.this);
                            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
                            if(weatherObject.getString("main").equals("Clear")){
                                image.setImageResource(R.drawable.sunny);
                            }
                            else if(weatherObject.getString("main").equals("Clouds")){
                                image.setImageResource(R.drawable.cloudy);
                            }
                            else if(weatherObject.getString("description").equals("light rain")){
                                image.setImageResource(R.drawable.slight_drizzle);
                            }
                            else{
                                image.setImageResource(R.drawable.drizzle);
                            }

                            right.addView(image);
                            // Adds the view to the layout


                            // add temperature to rigth box
                            double temperature = Double.parseDouble(main.getString("temp")) - 273.15;
                            temperature = Math.round(temperature * 100.0) / 100.0;
                            textView = new TextView(MainActivity.this);
                            textView.setId((int) System.currentTimeMillis());
                            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                            textView.setText(Double.toString(temperature) + (char) 0x00B0);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                            textView.setTextColor(Color.parseColor("#FFFFFF"));
                            right.addView(textView);

                            mainBox.addView(right);

//
//
//                        Log.i("sr. no.",Integer.toString(i)+"======>");
//                        JSONObject periodically=list.getJSONObject(i);
//                        Log.i("Date",periodically.getString("dt_txt"));
//
//                        date0.setText(periodically.getString("dt_txt"));
//
//                        JSONObject main=periodically.getJSONObject("main");
//                        Log.i("Temp",main.getString("temp"));
//                        double temperature=Double.parseDouble(main.getString("temp"))-273.15;
//                        temperature=Math.round(temperature * 100.0) / 100.0;
//                        tempr0.setText(Double.toString(temperature)+(char)0x00B0);
//                        Log.i("Temp_Min",main.getString("temp_min"));
//                        Log.i("Temp_Max",main.getString("temp_max"));
//
//                        Log.i("Pressure",main.getString("pressure"));
//                        pressure0.setText("Pressure: "+main.getString("pressure")+" hPa");
//
//                        Log.i("Humidity",main.getString("humidity"));
//                        humidity0.setText("Humidity: "+main.getString("humidity")+"%");
//
//                        JSONArray weatherArray=periodically.getJSONArray("weather");
//                        JSONObject weatherObject=weatherArray.getJSONObject(0);
//                        Log.i("Main",weatherObject.getString("main"));
//                        Log.i("Description",weatherObject.getString("description"));
//                        weather0.setText(weatherObject.getString("main")+": "+weatherObject.getString("description"));
//
//                        JSONObject cloud=periodically.getJSONObject("clouds");
//                        Log.i("All",cloud.getString("all"));
//
//                        JSONObject wind=periodically.getJSONObject("wind");
//                        Log.i("wind speed",wind.getString("speed"));
//                        wind0.setText("Wind: "+wind.getString("speed")+" m/s");

//                        Log.i("wind degree",wind.getString("deg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(MainActivity.this,"Incorrect City",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
