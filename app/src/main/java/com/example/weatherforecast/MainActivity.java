package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button btn_getCity;
    EditText txt_cityName;
    ListView lv_cityList;

    int count;
    String cityNode;
    String test;
    boolean exists;
    ArrayAdapter cityArrayAdapter;
    Database dataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //assign objects values to objects on layout
        btn_getCity = findViewById(R.id.btnAddCity);
        txt_cityName = findViewById(R.id.etxt_cityName);
        lv_cityList = findViewById(R.id.lv_cityList);
        dataBase = new Database(MainActivity.this);




        //capture text for city to be added

        btn_getCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the input from the user in the Text View and check if something has been added
                if(txt_cityName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this,"Enter a City First!", Toast.LENGTH_SHORT).show();
                }

                //checks if the city is already in the database
                count = dataBase.count(txt_cityName.getText().toString());
                if (count > 0)
            {
                Toast.makeText(MainActivity.this,"This City has already been added", Toast.LENGTH_SHORT).show();
            }
                else{
                    //Creates a city, retrieves the data and puts in the queue
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                City city = new City(-1, txt_cityName.getText().toString());
                String url = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=b6917a8cca6f4390aa9190109211504&num_of_days=5&format=json&q="+city.name;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    public void onResponse(JSONObject response) {

                        try {

                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONArray array = jsonObject.getJSONArray("request");
                            JSONObject node = array.getJSONObject(0);
                            cityNode = node.getString("query");

                            //check if a city has been found from the input
                            if(cityNode == "")
                            {
                                Toast.makeText(MainActivity.this,"Invalid City", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {

                                dataBase.addOne(city);
                                //Toast.makeText(MainActivity.this, city.name + " Added", Toast.LENGTH_SHORT).show();
                                txt_cityName.setText("");
                                hideKeyboard(MainActivity.this);
                                showCities(dataBase);
                                exists = true;
                                Toast.makeText(MainActivity.this,cityNode + " Added", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "false", Toast.LENGTH_SHORT).show();

                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(request);
                {
                    //creates new object


                }
            }}
        });

//tapping an item on the list, goes to the next activity
        lv_cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedCity = (String) parent.getItemAtPosition(position);
                    viewCity(view, clickedCity);

            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        showCities(dataBase);
    }

    //goes to the next activity and stores the string value of the item
    public void viewCity(View view, String city) {
        Intent intent = new Intent(this, Forecast.class);

        Bundle b = new Bundle();
        b.putString("cityname", city);
        intent.putExtras(b);
        startActivity(intent);
    }


//show the list of cities from the database

private void showCities(Database dataBase)
{
    cityArrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, dataBase.listAll());
    lv_cityList.setAdapter(cityArrayAdapter);
}

//hide the keyboard when a valid city has been added
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}