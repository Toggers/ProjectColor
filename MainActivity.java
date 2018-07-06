package com.tommy.mc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tommy.mc.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.*;

public class MainActivity extends AppCompatActivity {

    MediaPlayer song;
    View view;
    private boolean continueAnimation = false;
    Thread t;
    private int color = 1;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        view = this.getWindow().getDecorView();
        //view.setBackgroundResource(R.color.colorRed);

        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                loadColors();
               /* if (!continueAnimation) {
                    continueAnimation = true;
                    btn.setText("Stop");

                    song = MediaPlayer.create(MainActivity.this,R.raw.slowmusic);
                    song.start();

                    changeColorPeriodically();

                }else {
                    btn.setText("Start");
                    song.release();
                    continueAnimation = false;
                }*/
            }
        });
    }

    public View getView(){
        return view;
    }

    public void changeColorPeriodically() {
        new Thread() {
            public void run() {
                int intervalChange = 100;
                int intervalCheck = 10;
                int numChecksPerChange = intervalChange / intervalCheck;

                while (continueAnimation) {
                    if (intervalChange == numChecksPerChange){
                        changeColor2();
                        numChecksPerChange = 0;
                    }
                    else {
                        try {
                           sleep(intervalCheck);
                         } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        numChecksPerChange++;
                        if (!continueAnimation)
                             return;
                    }


                }

            }
        }.start();
    }

    void changeColor2(){
        runOnUiThread(new Runnable() {
            public void run() {
                if (color == 0)
                    view.setBackgroundResource(R.color.colorRed);
                if (color == 1)
                    view.setBackgroundResource(R.color.colorOrange);
                if (color == 2)
                    view.setBackgroundResource(R.color.colorYellow);
                if (color == 3)
                    view.setBackgroundResource(R.color.colorGreen);
                if (color == 4)
                    view.setBackgroundResource(R.color.colorBlue);
                if (color == 5) {
                    view.setBackgroundResource(R.color.colorPurple);
                    color = 0;
                    return;
                }
                color++;
            }
        });
    }

    public void loadColors() {
        Resources res = getResources();

        InputStream is = res.openRawResource(R.raw.twinkle);

        Scanner scanner = new Scanner(is);

        StringBuilder builder = new StringBuilder();

        while(scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
        }

        final List<ColorTime> cts = parseJson(builder.toString());

       new Thread() {
           public void run() {
               song = MediaPlayer.create(MainActivity.this,R.raw.slowmusic);
               song.start();

               for (int i = 0; i < cts.size(); i++) {

                   cts.get(i).changeColor(view, MainActivity.this);
               }

               song.release();
           }
       }.start();
    }

    private List<ColorTime> parseJson(String s) {
        List<ColorTime> array = new ArrayList<ColorTime>();

        try {
            JSONObject top = new JSONObject(s);
            String title = top.getString("title");
            JSONArray colors = top.getJSONArray("colors");

            if (colors != null) {
                for (int i=0;i<colors.length();i++){
                    JSONObject item = colors.getJSONObject(i);
                    ColorTime ct = new ColorTime(item.getString("color"), item.getInt("duration"));
                    array.add(ct);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }
}

class ColorTime{
    String color;
    int colRes;
    int duration;

    public ColorTime(String c, int t) {
        color = c;
        duration = t;
        if (c.equals("red"))
            colRes = R.color.colorRed;
        else if (c.equals("orange"))
            colRes = R.color.colorOrange;
        else if (c.equals("yellow"))
            colRes = R.color.colorYellow;
        else if (c.equals("green"))
            colRes = R.color.colorGreen;
        else if (c.equals("blue"))
            colRes = R.color.colorBlue;
        else if (c.equals("purple"))
            colRes = R.color.colorPurple;
    }

    public void changeColor(final View v, Activity act) {
        act.runOnUiThread(new Runnable() {
            public void run() {
                v.setBackgroundResource(colRes);
            }
        });
        try {
            sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

