package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.List;

public class Forecast extends AppCompatActivity {

    //Title at the top
    TextView cityName;
    //delete button
    Button btn_delete;
    Database database;

    //Name of the city the user is viewing
    String cityView;
    //numeric value of the today's day of the week
    int currentDay;
    //which day of the 5 day forecast the user is viewing
    int day;

    //the name of each day in the 5 day forecast
    String today; String second; String third; String fourth; String fifth;
    //buttons to select which day in the forecast to view
    Button btn_day1; Button btn_day2; Button btn_day3; Button btn_day4; Button btn_day5;
    //text view for each time of the day, txt_w0 is the current temperature for today
    TextView txt_w0; TextView txt_w1; TextView txt_w2; TextView txt_w3; TextView txt_w4; TextView txt_w5; TextView txt_w6; TextView txt_w7; TextView txt_w8;

    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast1);
        //pick up the string value of the city the user selected in the previous screen
        Bundle bundle = getIntent().getExtras();
        //assign to local String
        cityView = bundle.getString("cityname");
        database = new Database(Forecast.this);
        //assign objects
        btn_delete = findViewById(R.id.btnDelete);
        cityName = findViewById(R.id.txtCity);
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        cityName.setText(cityView);
        today = "Today";
        //activity starts with viewing today's forecast
        day = 1;
        //assign button objects to layout
        btn_day1 = findViewById(R.id.first); btn_day2 = findViewById(R.id.second); btn_day3 = findViewById(R.id.third); btn_day4 = findViewById(R.id.fourth); btn_day5 = findViewById(R.id.fifth);
        //assign TextViews to layout
        txt_w0 = findViewById(R.id.txt_w0); txt_w1 = findViewById(R.id.txt_w1); txt_w2 = findViewById(R.id.txt_w2); txt_w3 = findViewById(R.id.txt_w3); txt_w4 = findViewById(R.id.txt_w4);
        txt_w5 = findViewById(R.id.txt_w5); txt_w6 = findViewById(R.id.txt_w6); txt_w7 = findViewById(R.id.txt_w7); txt_w8 = findViewById(R.id.txt_w8);
        //TextView to display which day the user is currently viewing
        date = findViewById(R.id.txt_date);
        //delete city and return to previous activity
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCity(cityView);
                back(v);
            }
        });

        //buttons to change which day in the 5 day forecast the user is viewing
        btn_day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               today();
            }
        });
        btn_day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSecond();
            }
        });
        btn_day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThird();
            }
        });
        btn_day4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFourth();
            }
        });
        btn_day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFifth();
            }
        });

        //initialise what days are part of the 5 day forecast and set the text for buttons accordingly
        calculateDays();
        buttonNames();
        //show the forecast for today's weather
        setView(day);
        //get today's forecast data from the RequestQueue and set the TextView's text values accordingly
        getWeather(cityView);

    }


    private void getWeather(String cityView)
    {

        //take the city name and make a request
        RequestQueue queue = Volley.newRequestQueue(Forecast.this);
        String url = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=b6917a8cca6f4390aa9190109211504&num_of_days=5&format=json&q="+cityView;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject response) {
                String city = "";

                try {

                    JSONObject jsonObject = response.getJSONObject("data");
                    JSONArray array = jsonObject.getJSONArray("request");
                    JSONObject node = array.getJSONObject(0);
                    city = node.getString("query");

                } catch (JSONException e) {
                    Toast.makeText(Forecast.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                //if the day is 1, meaning today's forecast, display the current weather for this city
                if(day == 1)
                {
                    String current = "";
                    try{
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONArray array = jsonObject.getJSONArray("current_condition");
                        JSONObject node = array.getJSONObject(0);
                        current = node.getString("temp_C");
                        txt_w0.setText("Current temperature: " +current + " °C");

                    } catch (JSONException e) {
                        Toast.makeText(Forecast.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                //otherwise keep the text blank
                else{
                    txt_w0.setText("");
                }
                try{
                    //depending on the day the user is viewing, display the weather forecast
                        switch(day)
                        {
                            case 1:
                                setWeather(response, 0);
                                break;
                            case 2:
                                setWeather(response, 1);
                                break;
                            case 3:
                                setWeather(response, 2);
                                break;
                            case 4:
                                setWeather(response, 3);
                                break;
                            case 5:
                                setWeather(response, 4);
                                break;
                        }


                }catch (Exception e)
                {
                    Toast.makeText(Forecast.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

                    cityName.setText(city);

                }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Forecast.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });



        queue.add(request);


    }

    public void deleteCity(String city)
    {

        try {
            database.delete(city);
        }
        catch (Exception e)
        {
            Toast.makeText(Forecast.this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        Toast.makeText(Forecast.this, city + " Deleted!", Toast.LENGTH_SHORT).show();
    }

    //go back to the previous activity
    public void back(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    //naive method of calculating which days are to be included in the forecast
    private void calculateDays(){
        switch(currentDay)
        {
            case Calendar.SUNDAY:
                second = "Monday";
                third = "Tuesday";
                fourth = "Wednesday";
                fifth = "Thursday";
                break;
            case Calendar.MONDAY:
                second = "Tuesday";
                third = "Wednesday";
                fourth = "Thursday";
                fifth = "Friday";
                break;
            case Calendar.TUESDAY:
                second = "Wednesday";
                third = "Thursday";
                fourth = "Friday";
                fifth = "Saturday";
                break;
            case Calendar.WEDNESDAY:
                second = "Thursday";
                third = "Friday";
                fourth = "Saturday";
                fifth = "Sunday";
                break;
            case Calendar.THURSDAY:
                second = "Friday";
                third = "Saturday";
                fourth = "Sunday";
                fifth = "Monday";
                break;
            case Calendar.FRIDAY:
                second = "Saturday";
                third = "Sunday";
                fourth = "Monday";
                fifth = "Tuesday";
                break;
            case Calendar.SATURDAY:
                second = "Sunday";
                third = "Monday";
                fourth = "Tuesday";
                fifth = "Wednesday";
                break;
        }
    }

    //set the text of the buttons accordingly to the days included in the forecast
    private void buttonNames(){
        btn_day2.setText(second);
        btn_day3.setText(third);
        btn_day4.setText(fourth);
        btn_day5.setText(fifth);
    }

    //update the date Text View
    private void setView(int day)
    {
        switch(day)
        {
            case 1:
                date.setText("Today");
                break;
            case 2:
                date.setText(second);
                break;
            case 3:
                date.setText(third);
                break;
            case 4:
                date.setText(fourth);
                break;
            case 5:
                date.setText(fifth);
                break;
        }

    }

    //set view for when the user selects a day
    public void today()
    {

        day = 1;
        getWeather(cityView);
        setView(day);
    }
    public void setSecond()
    {
        day = 2;
        getWeather(cityView);
        setView(day);
    }
    public void setThird()
    {
        day = 3;
        getWeather(cityView);
        setView(day);
    }
    public void setFourth()
    {
        day = 4;
        getWeather(cityView);
        setView(day);
    }
    public void setFifth()
    {
        day = 5;
        getWeather(cityView);
        setView(day);
    }

    //get the forecast data by passing i, the index in the JSONArray, and set the text of the TextViews to the data
    private void setWeather(JSONObject response, int i) throws JSONException {
        JSONObject jsonObject = response.getJSONObject("data");
        JSONArray array = jsonObject.getJSONArray("weather");

        JSONObject root = array.getJSONObject(i);
        JSONArray mother = root.getJSONArray("hourly");
        JSONObject child = mother.getJSONObject(0);
        String node = child.getString("tempC");
        txt_w1.setText("00:00 " + node + " °C");

        JSONObject child2 = mother.getJSONObject(1);
        String node2 = child2.getString("tempC");
        txt_w2.setText("03:00 " + node2 + " °C");

        JSONObject child3 = mother.getJSONObject(2);
        String node3 = child3.getString("tempC");
        txt_w3.setText("06:00 " + node3 + " °C");

        JSONObject child4 = mother.getJSONObject(3);
        String node4 = child4.getString("tempC");
        txt_w4.setText("09:00 " + node4 + " °C");

        JSONObject child5 = mother.getJSONObject(4);
        String node5 = child5.getString("tempC");
        txt_w5.setText("12:00 " + node5 + " °C");

        JSONObject child6 = mother.getJSONObject(5);
        String node6 = child6.getString("tempC");
        txt_w6.setText("15:00 " + node6 + " °C");

        JSONObject child7 = mother.getJSONObject(6);
        String node7 = child7.getString("tempC");
        txt_w7.setText("18:00 " + node7 + " °C");

        JSONObject child8 = mother.getJSONObject(6);
        String node8 = child8.getString("tempC");
        txt_w8.setText("21:00 " + node8 + " °C");

    }

}