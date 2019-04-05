package com.example.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.musicplayer.R.layout.activity_player;

public class PlayerActivity extends AppCompatActivity {
    Button btn_next,btn_previous,btn_pause;
    TextView songTextLabel,positionStart,positionEnd;
    SeekBar songSeekBar;

    String sName;
    static MediaPlayer myMediaPlayer;
    int position;

    ArrayList<File> mySongs;
    Thread updateseekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_player);

        positionEnd=(TextView)findViewById(R.id.positionEnd);
        btn_next=(Button)findViewById(R.id.next);
        positionStart=(TextView) findViewById(R.id.positionStart);
        btn_previous=(Button)findViewById(R.id.previous);
        btn_pause=(Button)findViewById(R.id.pause);
        songTextLabel=(TextView)findViewById(R.id.songLabel);
        songSeekBar=(SeekBar)findViewById(R.id.seekbar);

        // Setting Action Bar
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // To set Seek Bar progress
        updateseekbar=new Thread(){
        @Override
        public void run()
        {
                    int totalDuration = myMediaPlayer.getDuration();
                    String dur=String.valueOf(totalDuration/60000)+":"+String.valueOf(totalDuration%1000);
                    positionEnd.setText(String.valueOf(dur));
                    int currentPosition=0;

                    while(currentPosition<totalDuration) {
                            currentPosition=myMediaPlayer.getCurrentPosition();
                            //positionStart.setText(currentPosition);
                            songSeekBar.setProgress(currentPosition);//

                    }
                }
        };

        // If there is no song playing then this is called
        if(myMediaPlayer!=null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        // To get Data sent from Main Activity
        Intent i =getIntent();
        Bundle bundle=i.getExtras();

        // get arraylist containinf song file
        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");

        //getting
        sName=mySongs.get(position).getName().toString();

        // Getting song name
        String songName = i.getStringExtra("songname");

        // Setting song name in textView
        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        //Getting song position in arraylist
        position=bundle.getInt("pos",0);

        // Getting song file to play
        Uri u = Uri.parse(mySongs.get(position).toString());

        // Setting myMediaPlayer to play song
        myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

        myMediaPlayer.start();
        songSeekBar.setMax(myMediaPlayer.getDuration());

        updateseekbar.start();

        // Changing color of seekbar
        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.MULTIPLY);

        // Updating seekbar on changes by user
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String dur=String.valueOf(progress/1000);
                positionStart.setText(dur);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        // on clicking pause Button
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekBar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.ic_play);
                    myMediaPlayer.pause();
                }
                else {
                    btn_pause.setBackgroundResource(R.drawable.ic_pause);
                    myMediaPlayer.start();
                }
            }
        });

        // On clicking next Button
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();

                //chaning position
                position=(position+1)%mySongs.size();

                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                // Setting songname
                sName=mySongs.get(position).getName().toLowerCase();
                songTextLabel.setText(sName);

                myMediaPlayer.start();
            }
        });

        // On clicking previous button
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMediaPlayer.stop();

                position=(position-1)<0?(mySongs.size()-1):(position-1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                // Setting songname
                sName=mySongs.get(position).getName().toLowerCase();
                songTextLabel.setText(sName);

                myMediaPlayer.start();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
