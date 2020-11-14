package com.lin.dream.weaterview;

import android.os.Bundle;
import android.widget.Toast;

import com.lin.dream.weatherview.WeatherDaysView;
import com.lin.dream.weatherview.entity.LinDate;
import com.lin.dream.weatherview.entity.IWeather;
import com.lin.dream.weatherview.listener.ItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ItemClickListener {

    WeatherDaysView weatherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherLayout = findViewById(R.id.hourWeather);
        try {
            init();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void init() throws CloneNotSupportedException {
        List<IWeather> entities = new ArrayList<>();
        List<IWeather> nightEntities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            WeatherEntity weatherEntity = new WeatherEntity();
            weatherEntity.temp = random.nextInt(10) + 20;
            weatherEntity.humidity = 24;
            weatherEntity.windDir = "西北风";
            weatherEntity.windLevel = 4;
            weatherEntity.timeStamp = System.currentTimeMillis();
            entities.add(weatherEntity);
            WeatherEntity weatherNightEntity = (WeatherEntity) weatherEntity.clone();
            weatherNightEntity.temp = weatherEntity.temp - 9 - random.nextInt(5);
            nightEntities.add(weatherNightEntity);
        }
        weatherLayout.setDayWeather(entities)
                .setNightWeather(nightEntities)
                .setListener(this)
                .init();
    }

    @Override
    public void onItemClick(IWeather weather) {

    }

    @Override
    public void onItemClick(IWeather dayWeather, IWeather nightWeather) {
        Toast.makeText(this, dayWeather.date().toString(), Toast.LENGTH_SHORT).show();
    }

    static class WeatherEntity implements IWeather, Cloneable {
        int temp;
        int humidity;
        String weatherDesc;
        String windDir;
        int windLevel;
        long timeStamp;

        @Override
        public int temperature() {
            return temp;
        }

        @Override
        public int humidity() {
            return humidity;
        }

        @Override
        public String airQuality() {
            return "";
        }

        @Override
        public String somatosensory() {
            return "";
        }

        @Override
        public String weatherDescription() {
            return weatherDesc;
        }

        @Override
        public String windDirection() {
            return windDir;
        }

        @Override
        public int windLevel() {
            return windLevel;
        }

        @Override
        public LinDate date() {
            return new LinDate(timeStamp);
        }

        @NonNull
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("\"temp\":")
                    .append(temp);
            sb.append(",\"humidity\":")
                    .append(humidity);
            sb.append(",\"weatherDesc\":\"")
                    .append(weatherDesc).append('\"');
            sb.append(",\"windDir\":\"")
                    .append(windDir).append('\"');
            sb.append(",\"windLevel\":")
                    .append(windLevel);
            sb.append(",\"timeStamp\":")
                    .append(timeStamp);
            sb.append('}');
            return sb.toString();
        }
    }

}