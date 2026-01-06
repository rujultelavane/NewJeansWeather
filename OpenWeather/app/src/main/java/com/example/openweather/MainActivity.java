package com.example.openweather;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button enter;
    EditText zipText;
    TextView descView, locationView, dateView, tempView, quoteView;
    ImageView pic;
    ArrayList<JSONObject> forecast;
    MediaPlayer cwyPlayer, dittoPlayer, etaPlayer, getupPlayer, supershyPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cwyPlayer=MediaPlayer.create(this, R.raw.cool_with_you);
        dittoPlayer=MediaPlayer.create(this, R.raw.ditto);
        etaPlayer=MediaPlayer.create(this, R.raw.eta);
        getupPlayer=MediaPlayer.create(this, R.raw.get_up);
        supershyPlayer=MediaPlayer.create(this, R.raw.super_shy);

        zipText=findViewById(R.id.editTextZipCode);
        enter = findViewById(R.id.buttonEnter);
        descView=findViewById(R.id.textViewDescription);
        locationView=findViewById(R.id.textViewLocation);
        tempView = findViewById(R.id.textViewTemp);
        quoteView = findViewById(R.id.textViewQuote);
        pic=findViewById(R.id.imageView2);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncThread task = new AsyncThread();
                if(forecast!=null && !forecast.isEmpty()){
                    forecast.clear();
                }
                if(zipText!=null && zipText.getText().toString().length()==5)
                    task.execute(zipText.getText().toString());
                else Toast.makeText(getApplicationContext(), "please enter a valid ZIP code!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class AsyncThread extends AsyncTask<String, Void, Void> {  //create and run a background thread to download the current weather
        String zipCode, countryCode, latitude, longitude, location, locationCoord;
        @Override
        protected Void doInBackground(String... strings) {
            forecast = new ArrayList<>();
                zipCode = strings[0];
            countryCode = "US";

            try {
                //GEOCODING
                URL newURL = new URL("https://api.openweathermap.org/geo/1.0/zip?zip="+zipCode+",US&appid=ba9a53057b0003b8a07861541cfdfe86"); //step 1 - instance of the URL class calling the constructor sending api call.
                URLConnection urlConnection = newURL.openConnection(); //step 2
                InputStream inputStream = urlConnection.getInputStream(); //step 3
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); //step 4
                String weatherData = bufferedReader.readLine(); //step 5
                JSONObject data = new JSONObject(weatherData);
                latitude = ""+String.format("%.2f", (data.getDouble("lat")));
                longitude = ""+String.format("%.2f", (data.getDouble("lon")));
                location = data.getString("name");
                locationCoord = location +" -- ("+latitude+", "+longitude+")";
                bufferedReader.close();

                //5 DAY/3 HOUR
                URL newURL2 = new URL("https://api.openweathermap.org/data/2.5/forecast?lat="+latitude+"&lon="+longitude+"&appid=ba9a53057b0003b8a07861541cfdfe86&units=imperial"); //step 1 //&units=imperial is to get to Farenheit
                URLConnection urlConnection2 = newURL2.openConnection(); //step 2
                InputStream inputStream2 = urlConnection2.getInputStream(); //step 3
                BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2)); //step 4
                String weatherData2 = bufferedReader2.readLine();
                JSONObject data2 = new JSONObject(weatherData2);
                bufferedReader2.close();

                JSONArray days= data2.getJSONArray("list");
                for(int x=0; x<40; x+=8)
                    forecast.add(days.getJSONObject(x));

            } catch (IOException | JSONException e) {   e.printStackTrace();}
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            try {
                if(forecast.size()!=0) //to make sure the zipcode is valid
                {
                    String description = forecast.get(0).getJSONArray("weather").getJSONObject(0).getString("description").toString();
                    String temp = ""+forecast.get(0).getJSONObject("main").getDouble("temp")+"º F";
                    String date, min, max;

                    locationView.setText(locationCoord);
                    tempView.setText(temp);
                    descView.setText(description);

                    setPic(description, pic);
                    setQuote(description, quoteView);

                    TextView date0, date1, date2, date3, date4;
                    ImageView pic0, pic1, pic2, pic3, pic4;
                    TextView weather0, weather1, weather2, weather3, weather4;
                    TextView min0, min1, min2, min3, min4;
                    TextView max0, max1, max2, max3, max4;

                    date0=findViewById(R.id.textViewDateDay0);
                    date1=findViewById(R.id.textViewDateDay1);
                    date2=findViewById(R.id.textViewDateDay2);
                    date3=findViewById(R.id.textViewDateDay3);
                    date4=findViewById(R.id.textViewDateDay4);

                    pic0=findViewById(R.id.imageViewPicDay0);
                    pic1=findViewById(R.id.imageViewPicDay1);
                    pic2=findViewById(R.id.imageViewPicDay2);
                    pic3=findViewById(R.id.imageViewPicDay3);
                    pic4=findViewById(R.id.imageViewPicDay4);

                    weather0=findViewById(R.id.textViewDescriptionDay0);
                    weather1=findViewById(R.id.textViewDescriptionDay1);
                    weather2=findViewById(R.id.textViewDescriptionDay2);
                    weather3=findViewById(R.id.textViewDescriptionDay3);
                    weather4=findViewById(R.id.textViewDescriptionDay4);

                    min0=findViewById(R.id.textViewLoDay0);
                    min1=findViewById(R.id.textViewLoDay1);
                    min2=findViewById(R.id.textViewLoDay2);
                    min3=findViewById(R.id.textViewLoDay3);
                    min4=findViewById(R.id.textViewLoDay4);

                    max0=findViewById(R.id.textViewHiDay0);
                    max1=findViewById(R.id.textViewHiDay1);
                    max2=findViewById(R.id.textViewHiDay2);
                    max3=findViewById(R.id.textViewHiDay3);
                    max4=findViewById(R.id.textViewHiDay4);

                    for(int x=0; x<forecast.size(); x++)
                    {
                        date = forecast.get(x).getString("dt_txt").substring(5, 10);
                        description = forecast.get(x).getJSONArray("weather").getJSONObject(0).getString("description").toString();
                        min = ""+forecast.get(x).getJSONObject("main").getDouble("temp_min")+"º";
                        max = ""+forecast.get(x).getJSONObject("main").getDouble("temp_max")+"º";
                        if(x==0){
                            date0.setText(date);
                            setPic(description, pic0);
                            weather0.setText(description);
                            min0.setText(min);
                            max0.setText(max);
                        }
                        if(x==1){
                            date1.setText(date);
                            setPic(description, pic1);
                            weather1.setText(description);
                            min1.setText(min);
                            max1.setText(max);
                        }
                        if(x==2){
                            date2.setText(date);
                            setPic(description, pic2);
                            weather2.setText(description);
                            min2.setText(min);
                            max2.setText(max);
                        }
                        if(x==3){
                            date3.setText(date);
                            setPic(description, pic3);
                            weather3.setText(description);
                            min3.setText(min);
                            max3.setText(max);
                        }
                        if(x==4){
                            date4.setText(date);
                            setPic(description, pic4);
                            weather4.setText(description);
                            min4.setText(min);
                            max4.setText(max);
                        }

                    }
                }
                else Toast.makeText(getApplicationContext(), "please enter a valid ZIP code!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public void setPic(String description, ImageView pic)
        {
            if(description.contains("clear"))
                pic.setImageResource(R.drawable.danielle2);
            if(description.contains("rain"))
                pic.setImageResource(R.drawable.minji2);
            if(description.contains("snow"))
                pic.setImageResource(R.drawable.hyein2);
            if(description.contains("cloud"))
                pic.setImageResource(R.drawable.haerin2);
            if(description.contains("sun"))
                pic.setImageResource(R.drawable.hanni2);
        }
        public void setQuote(String description, TextView quote)
        {
            if(cwyPlayer.isLooping())
                cwyPlayer.pause();
            if(dittoPlayer.isLooping())
                dittoPlayer.pause();
            if(etaPlayer.isLooping())
                etaPlayer.pause();
            if(getupPlayer.isLooping())
                getupPlayer.pause();
            if(supershyPlayer.isLooping())
                supershyPlayer.pause();

            if(description.contains("clear")) {
                quote.setText("\"we can go wherever you like, baby say the words and i'm down\"");
                etaPlayer.start();
                etaPlayer.setLooping(true);
            }
            if(description.contains("rain")) {
                quote.setText("\"말해줘, say it back oh say it ditto\"");
                dittoPlayer.setLooping(true);
                dittoPlayer.start();
            }
            if(description.contains("snow")) {
                quote.setText("\tyou may be on my mind, everyday baby, say you're mine\"");
                cwyPlayer.start();
                cwyPlayer.setLooping(true);
            }
            if(description.contains("cloud")) {
                quote.setText("\"get up, i don't wanna fight your shadow\"");
                getupPlayer.start();
                getupPlayer.setLooping(true);
            }
            if(description.contains("sun")) {
                quote.setText("\"Looking pretty, follow me, 우리 둘이 나란히\"");
                supershyPlayer.start();
                supershyPlayer.setLooping(true);
            }
        }
    }
}
