package com.example.administrator.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Field;

public class clearActivity extends AppCompatActivity {
    public static final String EXTRA_IS_CLEAR = "com.example.administrator.game.EXTRA.IS_CLEAR";
    public static final String EXTRA_BLOCK_COUNT ="com.example.administrator.game.EXTRA.BLOCK_COUNT";
    public static final String EXTRA_TIME = "com.example.administrator.game.EXTRA.EXTRA.TIME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent receiveIntent = getIntent();
        if(receiveIntent==null){
            finish();
        }

        Bundle receiveExtras = receiveIntent.getExtras();
        if(receiveExtras==null){
            finish();
        }

        boolean isClear = receiveExtras.getBoolean(EXTRA_IS_CLEAR,false);
        int blockCount = receiveExtras.getInt(EXTRA_BLOCK_COUNT,0);
        long clearTime = receiveExtras.getLong(EXTRA_TIME,0);

        TextView textTitle = (TextView) findViewById(R.id.textTitle);
        TextView textBlockCount = (TextView) findViewById(R.id.textBlockCount);
        TextView textClearTime = (TextView) findViewById(R.id.textClearTime);
        Button gameStart = (Button) findViewById(R.id.buttonGameStart);

        if(isClear){
            textTitle.setText(R.string.clear);
        }else{
            textTitle.setText(R.string.game_over);
        }

        textBlockCount.setText(getString(R.string.block_count,blockCount));
        textClearTime.setText(getString(R.string.time,clearTime/1000,clearTime%1000));

        gameStart.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent intent = new Intent(clearActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        TextView textScore = (TextView) findViewById(R.id.textScore);
        long score = (GameView.BLOCK_COUNT-blockCount) * clearTime;
        textScore.setText(getString(R.string.score,score));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long highScore = sharedPreferences.getLong("high_score",0);
        if(highScore<score){
            highScore = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("high_score",highScore);
            editor.commit();
        }
        TextView textHighScore = (TextView) findViewById(R.id.textHighScore);
        textHighScore.setText(getString(R.string.high_score,highScore));
         /*try{
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanenMenkey");
            if(menuKeyField!=null){
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config,false);
            }

        }catch (Exception ex){

        }*/


    }



}
